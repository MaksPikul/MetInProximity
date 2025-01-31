using MetInProximityBack.Models;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore;

namespace MetInProximityBack.Data
{
    public class AppDbContext : IdentityDbContext<AppUser>
    {
        public DbSet<Event> Event { get; set; }
        public DbSet<AppUser_Event> AppUserEvent { get; set; }

        public AppDbContext(DbContextOptions dbContextOptions)
        : base(dbContextOptions)
        {
        }

        protected override void OnModelCreating(ModelBuilder builder)
        {
            
                base.OnModelCreating(builder);

                builder.Entity<AppUser_Event>(x => x.HasKey(p => new { p.AppUserId, p.EventId }));
                builder.Entity<AppUser_Event>()
                    .HasOne(u => u.AppUser)
                    .WithMany(u => u.AppUserEvents)
                    .HasForeignKey(p => p.AppUserId);

                builder.Entity<AppUser_Event>()
                    .HasOne(u => u.Event)
                    .WithMany(u => u.AppUserEvents)
                    .HasForeignKey(p => p.EventId);

        }
    }
}
