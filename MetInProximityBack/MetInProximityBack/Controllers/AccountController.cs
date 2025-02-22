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
            AuthTokenService authTokenService,
            IOAuthService OAuthService

        ) { 
            _signInManager = signInManager;
            _userManager = userManager;
            _providerFactory = providerFactory;
            _authTokenService = authTokenService;
            _OAuthService = OAuthService;
        }

        [HttpPost("oauth/{provider}")]
        public async Task<IActionResult> Authenticate(
            [FromBody] AuthRequest authRequest,
            [FromRoute] string provider
        ) {

            try
            {
                // Factory which returns a OAuthProvider class, 
                // this class contains methods and variables required to complete oauth 
                IOAuthProvider OAuthProvider = _providerFactory.GetProvider(provider);

                //var authCode = authRequest.AuthCode;
                //var codeVerifier = authRequest.CodeVerifier;

                //authCode = HttpUtility.UrlDecode(authCode);
                // OAuth service using access code is now able to pass authentication when making requests to provider resource server (resource being user information)
                // OAuthTokenResponse tokens = await _OAuthService.GetOAuthTokens(OAuthProvider.TokenUrl, OAuthProvider.GetReqValues(authCode, codeVerifier));

                // Provider resource server returns a json of values, the Id token holding information about the user that we can use to authenticate them to our application
                // the id_token is a JWT which can be simply decoded into a enumerable object of claims
                // Claims are statements about a user, used by ASP.Net for authentication
                IEnumerable<Claim> claims = _authTokenService.DecodeToken(authRequest.IdToken);

                // Different providers id_tokens hold a differnt set of values, example, google has email key {email:"mail"} and microsoft as {mail : "mail"}
                // using the Adapter Design pattern, we can unify the many interfaces to a common one that can be used by the server

                OAuthUserDto user = await OAuthProvider.MapResponseToUser(claims);

                // EmailVerification handled by OAuth provider, less work for us to handle forgotten passwords and unverified or bot emails
                if (user.IsEmailVerified != true)
                {
                    return BadRequest("Email not verified.");
                }

                // This part simply checks if user has logged in with such credentials before, if not a new account is made,
                // A user who logs in with google can also log in with same emailed microsoft account, 
                // If a client would not like this feature,we can change code for accounts to hold a value in database to note what oauth provider was used for this account, and check if they match
                AppUser appUser = await _userManager.FindByEmailAsync(user.UserEmail); 

                if (appUser == null) {
                    appUser = await this.CreateAppUser(user.UserName, user.UserEmail, "fcmToken");
                }
                else
                {
                    //appUser.FcmToken = fcmToken;
                    await _userManager.UpdateAsync(appUser);
                }

                await _signInManager.SignInAsync(appUser, isPersistent: true);
                
                // Token for accessing app resources 30 minutes duration
                string accessToken = _authTokenService.CreateAccessToken(User); 
                // Token for refreshing access token 1 month duration
                string refreshToken = _authTokenService.CreateRefreshToken(User);

                // OPTIONAL, PROBABLY WILL INCLUDE, STORE refreshToken into DB to REVOKE access by admin

                return Ok(new {accessToken, refreshToken});
            }
            catch (ArgumentException ex)
            {
                return BadRequest("Login Failure: " + ex.Message );
            }
        }

        [HttpPost("logout")]
        public async Task Logout()
        {
            // Clear refresh token from DB
            await _signInManager.SignOutAsync();
        }

        [HttpPost("refresh")]
        public async Task<IActionResult> Refresh(
            [FromBody] string refreshToken
        ) {
            try {

                // Below wont work for security reasons, they must be validated
                ClaimsPrincipal principle = _authTokenService.ValidateToken(refreshToken);

                var tokenId = principle.Claims.GetClaimValue("TokenId");
                var userId = principle.Claims.GetClaimValue("UserId");

                AppUser user = await _userManager.FindByIdAsync(userId);

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


        //CLASS METHOD FOR NOW, WILL MOVE LATER, IF NECESSARY
        private async Task<AppUser> CreateAppUser(string UserName, string Email, string fcmToken)
        {
            AppUser appUser = new AppUser {UserName = UserName, Email = Email/*, FcmToken = fcmToken*/ };
            await _userManager.CreateAsync(appUser);
            //await _userManager.AddToRoleAsync(appUser, "User");
            return appUser;
        }


        // I think this is the only place that this method will be used, hence a private controller class
        
    }
}
