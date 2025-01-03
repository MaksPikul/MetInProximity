﻿using Azure;
using MetInProximityBack.Factories;
using MetInProximityBack.Interfaces;
using MetInProximityBack.Models;
using MetInProximityBack.Services;
using MetInProximityBack.Types;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using System.Data.Common;
using System.Security.Claims;

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
            Console.WriteLine("Goes here");

            try
            {
                IOAuthProvider OAuthProvider = _providerFactory.GetProvider(provider);

                OAuthTokenResponse tokens = await _OAuthService.GetOAuthTokens(OAuthProvider.TokenUrl, OAuthProvider.GetReqValues(code));

                IEnumerable<Claim> claims = _tokenService.DecodeToken(tokens.id_token);

                OAuthUserDto user = await OAuthProvider.MapResponseToUser(claims);
                
                if (user.IsEmailVerified != true)
                {
                    return BadRequest("Email not verified.");
                }

                AppUser appUser = await _userManager.FindByEmailAsync(user.UserEmail) ?? await CreateAppUser(user.UserName, user.UserEmail);

                await _signInManager.SignInAsync(appUser, isPersistent: true);

                var token = "encode a token later";
                 
                return Redirect($"com.metinproximity.app://callback?status=success&token={token}");
            }
            catch (ArgumentException ex)
            {
                return Redirect($"com.metinproximity.app://callback?status=error&message={ex.Message}");
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





        //CLASS METHOD FOR NOW, WILL MOVE LATER, IF NECESSARY
        async Task<AppUser> CreateAppUser(string UserName,string Email)
        {
            AppUser appUser = new AppUser {UserName = UserName, Email = Email };
            await _userManager.CreateAsync(appUser);
            //await _userManager.AddToRoleAsync(appUser, "User");
            return appUser;
        }
    }
}
