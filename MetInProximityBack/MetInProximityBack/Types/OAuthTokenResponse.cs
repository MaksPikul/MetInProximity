namespace MetInProximityBack.Types
{
    public record OAuthTokenResponse
    (
        string access_token,
        string id_token,
        string refresh_token,
        int expires_in,
        string scope
    );
}
