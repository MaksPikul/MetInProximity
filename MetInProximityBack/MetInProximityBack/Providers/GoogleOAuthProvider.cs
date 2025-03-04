using MetInProximityBack.Interfaces;
using Microsoft.AspNetCore.Mvc;
using System.Net.Http;
using System;
using MetInProximityBack.Services;
using Azure.Core;
using System.Security.Claims;
using MetInProximityBack.NewFolder;
using MetInProximityBack.Types.OAuth;

namespace MetInProximityBack.Providers
{
    public class GoogleOAuthProvider : IOAuthProvider
    {

        private readonly IConfiguration _configuration;
        public string ProviderName => "google";
        [Obsolete("Function fufilled on client side")]
        public string TokenUrl => "https://oauth2.googleapis.com/token";
        [Obsolete("Function fufilled on client side")]
        public string UserUrl => "https://www.googleapis.com/oauthv2/v1/userinfo";

        public GoogleOAuthProvider(
            IConfiguration configuration
        )
        {
            _configuration = configuration;
        }

        [Obsolete("Function fufilled on client side")]
        public Dictionary<string, string> GetReqValues(string authCode, string codeVerifier)
        {
            var body = new Dictionary<string, string>
            {
                { "client_id",  _configuration["Auth:Google:ClientId"] },
                { "client_secret", _configuration["Auth:Google:ClientSecret"] },
                { "grant_type", "authorization_code" },
                { "code", authCode },
                { "redirect_uri", _configuration["Auth:Google:RedirectUri"] },
                //{ "code_verifier", codeVerifier }
            };

            return body;
        }

        public async Task<OAuthUserDto> MapResponseToUser(IEnumerable<Claim> res)
        {
            var userEmail = res.GetClaimValue("email");
            var userName = res.GetClaimValue("name");

            return new OAuthUserDto
            {
                UserName = userName,
                UserEmail = userEmail,
                IsEmailVerified = userEmail == null ? false : true,
            };
        }
    }
}
