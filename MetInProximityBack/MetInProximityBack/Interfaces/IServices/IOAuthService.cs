using MetInProximityBack.Types.OAuth;

namespace MetInProximityBack.Interfaces.IServices
{
    public interface IOAuthService
    {
        Task<OAuthTokenResponse> GetOAuthTokens(string url, Dictionary<string, string> req);
        //Task<HttpResponseMessage> GetUserAsResponse(string url, string accessToken);
    }
}
