using MetInProximityBack.Types.OAuth;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;

namespace MetInProximityBack.Interfaces
{
    public interface IOAuthProvider
    {
        string ProviderName { get; }
        string TokenUrl { get; }
        string UserUrl { get; }
        Dictionary<string, string> GetReqValues(string authCode, string codeVerifier);
        Task<OAuthUserDto> MapResponseToUser(IEnumerable<Claim> res);
    }
}
