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

        public static string GetId(this ClaimsPrincipal user)
        {
            var idClaim = user.Claims.SingleOrDefault(x => x.Type == ClaimTypes.NameIdentifier);
            return idClaim?.Value;
        }

        public static string GetName(this ClaimsPrincipal user)
        {
            var nameClaim = user.Claims.SingleOrDefault(x => x.Type == ClaimTypes.Name);
            return nameClaim?.Value;
        }

        public static string GetOpenToPrivate(this ClaimsPrincipal user)
        {
            var otpClaim = user.Claims.SingleOrDefault(x => x.Type == "OpenToPrivate");
            return otpClaim?.Value;
        }
    }
}
