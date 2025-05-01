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
using MetInProximityBack.Extensions;
using Google.Apis.Logging;
using Microsoft.AspNetCore.Authentication.OAuth;

namespace MetInProximityBack.Controllers
{
    [Route("api/account")]
    [ApiController]
    public class AccountController : Controller
    {
        private readonly UserManager<AppUser> _userManager;
        private readonly OAuthProviderFactory _providerFactory;
        private readonly AuthTokenService _authTokenService;
        // private readonly IOAuthService _OAuthService; - No longer used -

        public AccountController(
            UserManager<AppUser> userManager,
            OAuthProviderFactory providerFactory,
            AuthTokenService authTokenService
        ) { 
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

                OAuthUserDto authUser = OAuthProvider.MapResponseToUser(claims);

                if (authUser.IsEmailVerified != true)
                {
                    return BadRequest("Email not verified.");
                }

                AppUser appUser = await this.CreateAppUser(authUser, authRequest.FcmToken);

                string accessToken = _authTokenService.CreateAccessToken(appUser); // 30 mins
                string refreshToken = _authTokenService.CreateRefreshToken(appUser); // 1 month

                // OPTIONAL, STORE refreshToken into DB to REVOKE access by admin

                return Ok(new {accessToken, refreshToken});
            }
            catch (ArgumentException ex)
            {
                return BadRequest("Login Failure: " + ex.Message );
            }
        }

        [HttpPost("refresh")]
        // Doesn't work for some reason
        public async Task<IActionResult> Refresh(
            [FromQuery] string refreshToken
        ) {
            try {

                ClaimsPrincipal principle = _authTokenService.ValidateToken(refreshToken);
                
                if (principle == null) {
                    throw new InvalidOperationException("Failed to refresh token, token not valid");
                }
                
                var tokenId = principle.Claims.GetClaimValue("TokenId");
                var userId = principle.Claims.GetClaimValue("nameid");

                AppUser? user = await _userManager.FindByIdAsync(userId);
                // User doesn't exist or token expired
                if (user == null)
                {
                    throw new InvalidOperationException("Failed to refresh token, user doesnt exist");
                }

                // OPTIONAL : Find token in DB
                var accessToken = _authTokenService.CreateAccessToken(user);

                // OPTIONAL : create new refresh token and send that back too
                return Ok(new { accessToken });
            }
            catch (Exception ex) {

                return StatusCode(500, "Failed to refresh: " + ex.Message);
            }
        }

        // I think this is the only place that this method will be used, hence a private controller class
        private async Task<AppUser> CreateAppUser(OAuthUserDto authUser, string fcmToken)
        {


            AppUser? appUser = await _userManager.FindByEmailAsync(authUser.UserEmail);

            if (appUser == null)
            {
                appUser = new AppUser { UserName = authUser.UserName, Email = authUser.UserEmail, FcmToken = fcmToken };
                await _userManager.CreateAsync(appUser);
            }
            else
            {
                if (appUser.FcmToken != fcmToken)
                {
                    appUser.FcmToken = fcmToken;
                    await _userManager.UpdateAsync(appUser);
                }
            }

            return appUser;
        }
    }
}
