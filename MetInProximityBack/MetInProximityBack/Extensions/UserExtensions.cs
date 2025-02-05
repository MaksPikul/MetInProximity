using System.Security.Claims;

namespace MetInProximityBack.Extensions
{
    public static class UserExtensions
    {
        public static string GetEmail(this ClaimsPrincipal user)
        {
            var emailClaim = user.Claims.SingleOrDefault(x => x.Type == ClaimTypes.Email);
            return emailClaim?.Value;
        }
    }
}
