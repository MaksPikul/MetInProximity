using Azure;
using MetInProximityBack.Factories;
using MetInProximityBack.Interfaces;
using MetInProximityBack.Models;
using MetInProximityBack.Builders;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;
using MetInProximityBack.NewFolder;
using Azure.Core;
using MetInProximityBack.Types.OAuth;
using MetInProximityBack.Services.Tokens;
using System.Web;
using RTools_NTS.Util;
using MetInProximityBack.Interfaces.IServices;

namespace MetInProximityBack.Controllers
{
    [Route("api/account")]
    [ApiController]
    public class AccountController : Controller
    {
        private readonly SignInManager<AppUser> _signInManager;
        private readonly UserManager<AppUser> _userManager;
        private readonly OAuthProviderFactory _providerFactory;
        private readonly AuthTokenService _authTokenService;
        private readonly IOAuthService _OAuthService;

        public AccountController(
            SignInManager<AppUser> signInManager,
            UserManager<AppUser> userManager,
            OAuthProviderFactory providerFactory,
            AuthTokenService authTokenService

        ) { 
            _signInManager = signInManager;
            _userManager = userManager;
            _providerFactory = providerFactory;
            _authTokenService = authTokenService;
        }

        [HttpPost("oauth/{provider}")]
        public async Task<IActionResult> Authenticate(
            [FromBody] AuthRequest authRequest,
            [FromRoute] string provider
        ) {
            try
            {

                IOAuthProvider OAuthProvider = _providerFactory.GetProvider(provider);

                IEnumerable<Claim> claims = _authTokenService.DecodeToken(authRequest.IdToken);

                OAuthUserDto user = await OAuthProvider.MapResponseToUser(claims);

                // EmailVerification handled by OAuth provider, less work for us to handle forgotten passwords and unverified or bot emails
                if (user.IsEmailVerified != true)
                {
                    return BadRequest("Email not verified.");
                }

                AppUser appUser = await _userManager.FindByEmailAsync(user.UserEmail); 

                if (appUser == null) {
                    appUser = await this.CreateAppUser(user.UserName, user.UserEmail, authRequest.FcmToken);
                }
                else
                {
                    if (appUser.FcmToken != authRequest.FcmToken)
                    {
                        appUser.FcmToken = authRequest.FcmToken;
                        await _userManager.UpdateAsync(appUser);
                    }
                }

                await _signInManager.SignInAsync(appUser, isPersistent: true);
                
                // Token for accessing app resources 30 minutes duration
                string accessToken = _authTokenService.CreateAccessToken(User); 
                // Token for refreshing access token 1 month duration
                string refreshToken = _authTokenService.CreateRefreshToken(User);

                // OPTIONAL, STORE refreshToken into DB to REVOKE access by admin

                return Ok(new {accessToken, refreshToken});
            }
            catch (ArgumentException ex)
            {
                return BadRequest("Login Failure: " + ex.Message );
            }
        }

        [HttpPost("refresh")]
        public async Task<IActionResult> Refresh(
            [FromBody] string refreshToken
        ) {
            try {

                // Below wont work for security reasons, they must be validated
                ClaimsPrincipal principle = _authTokenService.ValidateToken(refreshToken);

                var tokenId = principle.Claims.GetClaimValue("TokenId");
                var userId = principle.Claims.GetClaimValue(ClaimTypes.NameIdentifier);

                AppUser? user = await _userManager.FindByIdAsync(userId);

                // User doesn't exist or token expired
                if (user == null)
                {
                    throw new InvalidOperationException("Failed to refresh token, user doesnt exist");
                }

                // Find token in DB

                var accessToken = _authTokenService.CreateAccessToken(user);

                // Optional : create new refresh token and send that back too

                return Ok(new { accessToken });
            }
            catch (Exception ex) {

                return StatusCode(500, "Failed to refresh: " + ex.Message);
            }
        }

        // I think this is the only place that this method will be used, hence a private controller class
        private async Task<AppUser> CreateAppUser(string UserName, string Email, string fcmToken)
        {
            AppUser appUser = new AppUser {UserName = UserName, Email = Email, FcmToken = fcmToken};
            await _userManager.CreateAsync(appUser);
            //await _userManager.AddToRoleAsync(appUser, "User");
            return appUser;
        }
    }
}
