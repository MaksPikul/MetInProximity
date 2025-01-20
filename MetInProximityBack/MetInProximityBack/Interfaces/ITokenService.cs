using System.Security.Claims;

namespace MetInProximityBack.Interfaces
{
    public interface ITokenService
    {
        string CreateToken(List<Claim> claims);
        IEnumerable<Claim> DecodeToken(string token);
    }
}
