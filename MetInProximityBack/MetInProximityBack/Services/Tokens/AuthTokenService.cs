using MetInProximityBack.Builders;
using MetInProximityBack.Interfaces;
using Microsoft.Azure.Cosmos;
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
                    .AddClaim("UserId", User.FindFirstValue(ClaimTypes.NameIdentifier))
                    .AddClaim("UserName", User.Identity.Name)
                    .AddClaim("OpenToPrivate", openToPrivate.ToString())
                    .AddClaim("Email", User.FindFirstValue(ClaimTypes.Email))
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

            var refreshToken = base.CreateToken(refreshTokenClaims, 60 * 24 * 30); // Month

            return refreshToken;
        }

    }
}
