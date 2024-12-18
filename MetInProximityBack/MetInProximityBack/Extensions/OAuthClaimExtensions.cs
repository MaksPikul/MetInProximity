using System.Security.Claims;

namespace MetInProximityBack.NewFolder
{
    public static class OAuthClaimExtensions
    {
        public static string GetClaimValue(this IEnumerable<Claim> claims, string claimType)
        {
            var claim = claims.FirstOrDefault(c => c.Type == claimType).Value;

            return claim;
        }
    }
}
