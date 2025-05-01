using MetInProximityBack.Builders;
using MetInProximityBack.Extensions;
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
                    .AddClaim(ClaimTypes.NameIdentifier, User.GetId())
                    .AddClaim(ClaimTypes.Name, User.GetName())
                    .AddClaim("OpenToPrivate", openToPrivate.ToString())
                    .AddClaim(ClaimTypes.Email, User.GetEmail())
                .Build();

            string accessToken = base.CreateToken(accessTokenClaims, 60 * 24 * 30); // 30 mins

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

            string accessToken = base.CreateToken(accessTokenClaims, 60 * 24 * 30); // 30 mins / days

            return accessToken;
        }

        public string CreateRefreshToken(AppUser User)
        {
            List<Claim> refreshTokenClaims = new ClaimsBuilder()
                    .AddClaim("TokenId", Guid.NewGuid().ToString()) // used to store in DB, and Revoke user access
                    .AddClaim(ClaimTypes.NameIdentifier, User.Id)
                .Build();

            var refreshToken = base.CreateToken(refreshTokenClaims, 60 * 24 * 30); // Month 43200

            return refreshToken;
        }

    }
}
