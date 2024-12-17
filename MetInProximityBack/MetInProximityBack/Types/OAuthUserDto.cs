namespace MetInProximityBack.Types
{
    public class OAuthUserDto
    {
        public string UserName  { get; set; } = string.Empty;
        public string UserEmail { get; set; } = string.Empty;
        public bool IsEmailVerified { get; set; }
    }
}
