using MetInProximityBack.Interfaces;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;

namespace MetInProximityBack.Services
{
    public class TokenService : ITokenService

    {

        public IEnumerable<Claim> DecodeToken(string token)
        {
            var handler = new JwtSecurityTokenHandler();

            var jwt = handler.ReadJwtToken(token);

            var claims = jwt.Claims;

            return claims;
        }
    }
}
