using System.Security.Claims;

namespace MetInProximityBack.Interfaces
{
    public interface ITokenService
    {
        IEnumerable<Claim> DecodeToken(string token);
    }
}
