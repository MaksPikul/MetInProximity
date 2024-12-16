using MetInProximityBack.Models;
using MetInProximityBack.ServiceInterfaces;
using MetInProximityBack.Services;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;

namespace MetInProximityBack.Controllers
{
    [Route("api/account")]
    [ApiController]
    public class AccountController : Controller
    {
        private readonly SignInManager<AppUser> _signInManager;

        private readonly ITokenService _tokenService;
        private readonly IOAuthService _OAuthService;
        public AccountController(
            SignInManager<AppUser> signInManager,
            ITokenService tokenService,
            IOAuthService OAuthService
        ) { 
            _signInManager = signInManager;
            _tokenService = tokenService;
            _OAuthService = OAuthService;
        }

        [HttpPost("googleLogin")]
        public async Task<IActionResult> googleOAuthLogin([FromQuery(Name = "code")] string code)
        {

            if (string.IsNullOrWhiteSpace(code))
            {
                return BadRequest("Authorization code is required.");
            }

            var tokens = await _OAuthService.GetGoogleOAuthTokens(code);
            if (tokens == null)
            {
                return BadRequest("Invalid Google tokens.");
            }

            var googleUserClaims = _tokenService.DecodeToken(tokens.id_token);
            if (googleUserClaims == null)
            {
                return BadRequest("Failed to decode Google user token.");
            }

            var isEmailVerified = googleUserClaims.FirstOrDefault(c => c.Type == "email_verified")?.Value;
            if (isEmailVerified != "true")
            {
                return BadRequest("Email not verified.");
            }

            //factory this
            //var appUser = await _userManager.FindByEmailAsync(userEmail) ?? await CreateAppUser(userName, userEmail, userPicture);

            await _signInManager.SignInAsync(appUser, isPersistent: true);

            return Redirect("https://localhost:5173/home");
        }


        [HttpPost("microsoftLogin")]
        public IActionResult microsoftOAuthLogin([FromQuery(Name = "code")] string code)
        {
            if (string.IsNullOrWhiteSpace(code)) return BadRequest("Authorization code is required.");








            return View();
        }

        [HttpPost("logout")]
        public async Task logout()
        {
            await _signInManager.SignOutAsync();
            Response.Redirect("https://localhost:5173/");

        }

        [HttpPost("ping")]
        public IActionResult ping()
        {
            return View();
        }
    }
}
