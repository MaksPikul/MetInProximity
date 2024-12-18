using MetInProximityBack.Interfaces;
using MetInProximityBack.NewFolder;
using MetInProximityBack.Types;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using System.Security.Claims;
using static Microsoft.EntityFrameworkCore.DbLoggerCategory;

namespace MetInProximityBack.Providers
{
    public class MicrosoftOAuthProvider : IOAuthProvider
    {
        private readonly IConfiguration _configuration;
        public string ProviderName => "microsoft";
        public string TokenUrl => "https://login.microsoftonline.com/common/oauth2/v2.0/token";
        public string UserUrl => "https://graph.microsoft.com/v1.0/me";

        public MicrosoftOAuthProvider(
            IConfiguration configuration
        ) 
        {
            _configuration = configuration;
        }

        public Dictionary<string, string> GetReqValues(string code)
        { 
            var req = new Dictionary<string, string>
            {
                { "client_id", _configuration["Auth:Microsoft:ClientId"] },
                { "client_secret", _configuration["Auth:Microsoft:ClientSecret"] },
                { "grant_type", "authorization_code" },
                { "code", code },
                { "response_mode", "query" },
                { "redirect_uri", _configuration["Auth:Microsoft:RedirectUri"] },
                { "scope", "openid profile email" }
            };

            return req;
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
