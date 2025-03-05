using MetInProximityBack.Types.OAuth;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;

namespace MetInProximityBack.Interfaces
{
    public interface IOAuthProvider
    {
        string ProviderName { get; }
        Task<OAuthUserDto> MapResponseToUser(IEnumerable<Claim> res);
    }
}
