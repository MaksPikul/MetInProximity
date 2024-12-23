using Microsoft.AspNetCore.Identity;

namespace MetInProximityBack.Models
{
    public class AppUser : IdentityUser
    {
        public List<AppUser_Event> AppUserEvents { get; set; } = new List<AppUser_Event>();
    }
}
