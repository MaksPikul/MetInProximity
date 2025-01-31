using Azure;
using MetInProximityBack.Factories;
using MetInProximityBack.Interfaces;
using MetInProximityBack.Models;
using MetInProximityBack.Builders;
using MetInProximityBack.Types;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;
using MetInProximityBack.NewFolder;
using Azure.Core;

namespace MetInProximityBack.Controllers
{
    [Route("api/account")]
    [ApiController]
    public class AccountController : Controller
    {
        private readonly SignInManager<AppUser> _signInManager;
        private readonly UserManager<AppUser> _userManager;
        private readonly OAuthProviderFactory _providerFactory;
        private readonly ITokenService _tokenService;
        private readonly IOAuthService _OAuthService;

        public AccountController(
            SignInManager<AppUser> signInManager,
            UserManager<AppUser> userManager,
            OAuthProviderFactory providerFactory,
            ITokenService tokenService,
            IOAuthService OAuthService

        ) { 
            _signInManager = signInManager;
            _userManager = userManager;
            _providerFactory = providerFactory;
            _tokenService = tokenService;
            _OAuthService = OAuthService;
        }

        [HttpPost("oauth/{provider}")]
        public async Task<IActionResult> Authenticate(
            [FromQuery(Name = "code")] string code, 
            [FromRoute] string provider
        ) {

            try
            {
                // Factory which returns a OAuthProvider class, 
                // this class contains methods and variables required to complete oauth 
                IOAuthProvider OAuthProvider = _providerFactory.GetProvider(provider);

                // OAuth service using access code is now able to pass authentication when making requests to provider resource server (resource being user information)
                OAuthTokenResponse tokens = await _OAuthService.GetOAuthTokens(OAuthProvider.TokenUrl, OAuthProvider.GetReqValues(code));

                // Provider resource server returns a json of values, the Id token holding information about the user that we can use to authenticate them to our application
                // the id_token is a JWT which can be simply decoded into a enumerable object of claims
                // Claims are statements about a user, used by ASP.Net for authentication
                IEnumerable<Claim> claims = _tokenService.DecodeToken(tokens.id_token);

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
                AppUser appUser = await _userManager.FindByEmailAsync(user.UserEmail) ?? await this.CreateAppUser(user.UserName, user.UserEmail);

                await _signInManager.SignInAsync(appUser, isPersistent: true);
                
                // Token for accessing app resources 30 minutes duration
                var accessToken = this.CreateAccessToken(); 
                // Token for refreshing access token 1 month duration
                var refreshToken = this.CreateRefreshToken();

                // OPTIONAL, PROBABLY WILL INCLUDE, STORE refreshToken into DB to REVOKE access by admin

                return Ok(new {accessToken, refreshToken});
            }
            catch (ArgumentException ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpPost("logout")]
        public async Task Logout()
        {
            // Clear refresh token from DB
            await _signInManager.SignOutAsync();
        }

        [HttpPost("ping")]
        public IActionResult Ping()
        {
            return View();
        }

        [HttpPost("refresh")]
        public async Task<IActionResult> Refresh(
            [FromBody] string refreshToken
        ) {

            IEnumerable<Claim> decodedToken = _tokenService.DecodeToken(refreshToken);

            var tokenId = decodedToken.GetClaimValue("TokenId");
            var expiration = decodedToken.GetClaimValue("expiration");
            var userId = decodedToken.GetClaimValue("UserId");

            AppUser user = await _userManager.FindByIdAsync(userId);

            // User doesn't exist or token expired
            if (user == null || DateTime.Parse(expiration) > DateTime.UtcNow) {
                return BadRequest("Go back to login");
            }

            // Find token in DB

            var accessToken = this.CreateAccessToken();

            // Optional : create new refresh token and send that back too

            return Ok(new { accessToken });
        }





        //CLASS METHOD FOR NOW, WILL MOVE LATER, IF NECESSARY
        private async Task<AppUser> CreateAppUser(string UserName,string Email)
        {
            AppUser appUser = new AppUser {UserName = UserName, Email = Email };
            //await _userManager.CreateAsync(appUser);
            //await _userManager.AddToRoleAsync(appUser, "User");
            return appUser;
        }


        // I think this is the only place that this method will be used, hence a private controller class
        private string CreateAccessToken()
        {
            List<Claim> accessTokenClaims = new ClaimsBuilder()
                    .AddClaim("TokenId", Guid.NewGuid().ToString())
                    .AddClaim("UserId", User.FindFirstValue(ClaimTypes.NameIdentifier))
                    .AddClaim("UserName", User.Identity.Name)
                    .AddClaim("Email", User.FindFirstValue(ClaimTypes.Email))
            .Build();

            string accessToken = _tokenService.CreateToken(accessTokenClaims, 30); // 30 mins

            return accessToken;
        }

        private string CreateRefreshToken()
        {
            List<Claim> refreshTokenClaims = new ClaimsBuilder()
                .AddClaim("UserId", User.FindFirstValue(ClaimTypes.NameIdentifier))
                .AddClaim("TokenId", Guid.NewGuid().ToString()) // used to store in DB, and Revoke user access
                .Build();

            var refreshToken = _tokenService.CreateToken(refreshTokenClaims, 60 * 24 * 30); // Month

            return refreshToken;
        }
    }
}
