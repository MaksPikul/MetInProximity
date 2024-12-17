using Azure;
using MetInProximityBack.Factories;
using MetInProximityBack.Interfaces;
using MetInProximityBack.Models;
using MetInProximityBack.Services;
using MetInProximityBack.Types;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using System.Data.Common;

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
                IOAuthProvider OAuthProvider = _providerFactory.GetProvider(provider);

                OAuthTokenResponse tokens = await _OAuthService.GetOAuthTokens(OAuthProvider.TokenUrl, OAuthProvider.GetReqValues(code));

                HttpResponseMessage response = await _OAuthService.GetUserAsResponse(OAuthProvider.UserUrl, tokens.access_token);

                OAuthUserDto user = await OAuthProvider.MapResponseToUser(response);

                if (user.IsEmailVerified != true)
                {
                    return BadRequest("Email not verified.");
                }

                //check for null after creation below here
                //factory this
                AppUser appUser = await _userManager.FindByEmailAsync(user.UserEmail); //?? await CreateAppUser(user.UserName, user.UserEmail);

                await _signInManager.SignInAsync(appUser, isPersistent: true);

                return Redirect("https://localhost:5173/home");

            }
            catch (ArgumentException ex)
            {
                return BadRequest(ex.Message);

            }
        }

        [HttpPost("logout")]
        public async Task Logout()
        {
            await _signInManager.SignOutAsync();
            Response.Redirect("https://localhost:5173/");

        }

        [HttpPost("ping")]
        public IActionResult Ping()
        {
            return View();
        }
    }
}
