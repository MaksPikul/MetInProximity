using MetInProximityBack.Interfaces.IServices;
using Microsoft.IdentityModel.Protocols.OpenIdConnect;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;

namespace MetInProximityBack.Services.Tokens
{
    public abstract class TokenService : ITokenService

    {
        protected readonly IConfiguration _config;
        protected readonly SymmetricSecurityKey _key;
        protected readonly JwtSecurityTokenHandler _tokenHandler;
        public TokenService(IConfiguration config)
        {
            _config = config;
            _key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_config["JWT:SigningKey"]));
            _tokenHandler = new JwtSecurityTokenHandler();
        }

        public IEnumerable<Claim> DecodeToken(string token)
        {

            var jwt = _tokenHandler.ReadJwtToken(token);

            var claims = jwt.Claims;

            foreach (var claim in jwt.Payload)
            {
                Console.WriteLine(claim.Key);
                Console.WriteLine(claim.Value);
            }

            return claims;
        }

        protected string CreateToken(List<Claim> claims, int mins)
        {

            var creds = new SigningCredentials(_key, SecurityAlgorithms.HmacSha512Signature);

            var tokenDescriptor = new SecurityTokenDescriptor
            {
                Subject = new ClaimsIdentity(claims),
                Expires = DateTime.Now.AddMinutes(mins),      // Keep, or set manually with claims builder?
                SigningCredentials = creds, 
                Issuer = _config["JWT:Issuer"],
                Audience = _config["JWT:Audience"]
            };

            var token = _tokenHandler.CreateToken(tokenDescriptor);

            return _tokenHandler.WriteToken(token);
        }

        public ClaimsPrincipal ValidateToken(string token)
        {
            var validationParams = new TokenValidationParameters
            {
                ValidateIssuerSigningKey = true,
                IssuerSigningKey = _key,
                ValidateIssuer = true,
                ValidIssuer = _config["JWT:Issuer"],
                ValidateAudience = true,
                ValidAudience = _config["JWT:Audience"],
                ValidateLifetime = true
            };
            try
            {
                _tokenHandler.InboundClaimTypeMap.Clear();
                ClaimsPrincipal validatedToken = _tokenHandler.ValidateToken(token, validationParams, out SecurityToken x);

                return validatedToken;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Token validation failed: {ex.Message}");
                return null;
            }
        }


    }
}
