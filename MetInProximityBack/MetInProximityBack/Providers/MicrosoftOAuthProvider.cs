using MetInProximityBack.Interfaces;
using MetInProximityBack.ServiceInterfaces;
using MetInProximityBack.Types;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;

namespace MetInProximityBack.Providers
{
    public class MicrosoftOAuthProvider : IOAuthProvider
    {
        private readonly IConfiguration _configuration;
        public string ProviderName => "microsoft";
        public string TokenUrl => "https://login.microsoftonline.com/{tenant}/oauth2/v2.0/token";
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
                { "redirect_uri", _configuration["Auth:Microsoft:RedirectUri"] },
                { "scope", "openid profile email" }
            };

            return req;
        }

        public async Task<OAuthUserDto> MapResponseToUser(HttpResponseMessage res)
        {
            MicrosoftOAuthUserResponse user = await res.Content.ReadFromJsonAsync<MicrosoftOAuthUserResponse>();

            return new OAuthUserDto
            {
                UserName = user.displayName,
                UserEmail = user.mail,
                IsEmailVerified = user.mail == null ? false : true,
            };

        }
    }
}
