using MetInProximityBack.Builders;
using MetInProximityBack.Interfaces;
using MetInProximityBack.Models;
using Microsoft.Azure.Cosmos;
using Microsoft.Azure.Cosmos.Linq;
using System.Security.Claims;
using System.Text;

namespace MetInProximityBack.Services.Tokens
{
    public class AuthTokenService : TokenService
    {

        public AuthTokenService(IConfiguration configuration) : base(configuration) { }


        public string CreateAccessToken(ClaimsPrincipal User, bool openToPrivate = false)
        {
            List<Claim> accessTokenClaims = new ClaimsBuilder()
                    .AddClaim("TokenId", Guid.NewGuid().ToString())
                    .AddClaim(ClaimTypes.NameIdentifier, User.FindFirstValue(ClaimTypes.NameIdentifier))
                    .AddClaim(ClaimTypes.Name, User.FindFirstValue(ClaimTypes.Name))
                    .AddClaim("OpenToPrivate", openToPrivate.ToString())
                    .AddClaim(ClaimTypes.Email, User.FindFirstValue(ClaimTypes.Email))
                .Build();

            string accessToken = base.CreateToken(accessTokenClaims, 30); // 30 mins

            return accessToken;
        }
        
        public string CreateAccessToken(AppUser User, bool openToPrivate = false)
        {
            List<Claim> accessTokenClaims = new ClaimsBuilder()
                    .AddClaim("TokenId", Guid.NewGuid().ToString())
                    .AddClaim(ClaimTypes.NameIdentifier, User.Id)
                    .AddClaim(ClaimTypes.Name, User.UserName)
                    .AddClaim("OpenToPrivate", openToPrivate.ToString())
                    .AddClaim(ClaimTypes.Email, User.Email)
                .Build();

            string accessToken = base.CreateToken(accessTokenClaims, 30); // 30 mins

            return accessToken;
        }

        public string CreateRefreshToken(ClaimsPrincipal User)
        {
            List<Claim> refreshTokenClaims = new ClaimsBuilder()
                    .AddClaim("TokenId", Guid.NewGuid().ToString()) // used to store in DB, and Revoke user access
                    .AddClaim("UserId", User.FindFirstValue(ClaimTypes.NameIdentifier))
                .Build();

            var refreshToken = base.CreateToken(refreshTokenClaims, 30); // Month 43200

            return refreshToken;
        }

    }
}
