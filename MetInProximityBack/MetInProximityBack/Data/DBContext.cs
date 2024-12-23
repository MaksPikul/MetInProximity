using MetInProximityBack.Models;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore;

namespace MetInProximityBack.Data
{
    public class DBContext : IdentityDbContext<AppUser>
    {
        public DbSet<Event> Event { get; set; }
        public DbSet<AppUser_Event> AppUserEvent { get; set; }

        public DBContext(DbContextOptions dbContextOptions)
        : base(dbContextOptions)
        {

        }
    }
}
