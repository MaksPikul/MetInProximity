using System.Security.Claims;

namespace MetInProximityBack.Interfaces
{
    public interface ITokenService
    {
        string CreateToken(List<Claim> claims, int mins);
        IEnumerable<Claim> DecodeToken(string token);
    }
}
