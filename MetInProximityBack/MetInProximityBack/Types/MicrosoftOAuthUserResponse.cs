namespace MetInProximityBack.Types
{
    public class MicrosoftOAuthUserResponse
    {
        // more about the response
        // https://learn.microsoft.com/en-us/graph/api/user-get?view=graph-rest-1.0&tabs=http

        public string displayName { get; set; } = string.Empty;
        public string mail { get; set; } = string.Empty;
    }
}
