using MetInProximityBack.Interfaces;
using MetInProximityBack.Types;
using Microsoft.AspNetCore.Mvc;
using System.Net.Http;
using System;
using MetInProximityBack.Services;
using Azure.Core;
using System.Security.Claims;
using MetInProximityBack.NewFolder;

namespace MetInProximityBack.Providers
{
    public class GoogleOAuthProvider : IOAuthProvider
    {

        private readonly IConfiguration _configuration;
        public string ProviderName => "google";
        public string TokenUrl => "https://oauth2.googleapis.com/token";
        public string UserUrl => "https://www.googleapis.com/oauthv2/v1/userinfo";

        public GoogleOAuthProvider(
            IConfiguration configuration
        )
        {
            _configuration = configuration;
        }

        public Dictionary<string, string> GetReqValues(string code)
        {
            var body = new Dictionary<string, string>
            {
                { "client_id",  _configuration["Auth:Google:ClientId"] },
                { "client_secret", _configuration["Auth:Google:ClientSecret"] },
                { "grant_type", "authorization_code" },
                { "code", code },
                { "redirect_uri", _configuration["Auth:Google:RedirectUri"] }
            };

            return body;
        }

        public async Task<OAuthUserDto> MapResponseToUser(IEnumerable<Claim> res)
        {
            var userEmail = res.GetClaimValue("email");
            var userName = res.GetClaimValue("name");
            var userVerified = res.GetClaimValue("email_verified"); 

            return new OAuthUserDto
            {
                UserName = userName,
                UserEmail = userEmail,
                IsEmailVerified = userVerified == "true" ? true : false,
            };

        }
    }
}
