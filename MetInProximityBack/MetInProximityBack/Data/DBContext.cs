using MetInProximityBack.Models;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore;

namespace MetInProximityBack.Data
{
    public class DBContext : IdentityDbContext<AppUser>
    {
        public DBContext(DbContextOptions dbContextOptions)
        : base(dbContextOptions)
        {

        }
    }
}
