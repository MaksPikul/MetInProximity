using MetInProximityBack.Interfaces;
using MetInProximityBack.Types;
using Microsoft.AspNetCore.Mvc;
using System.Net.Http;
using System;
using MetInProximityBack.Services;
using MetInProximityBack.ServiceInterfaces;
using Azure.Core;

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

        public async Task<OAuthUserDto> MapResponseToUser(HttpResponseMessage res)
        {
            GoogleOAuthUserResponse user = await res.Content.ReadFromJsonAsync<GoogleOAuthUserResponse>();

            return new OAuthUserDto
            {
                UserName = user.name,
                UserEmail = user.email,
                IsEmailVerified = user.email_verified,
            };

        }
    }
}
