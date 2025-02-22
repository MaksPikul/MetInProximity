using MetInProximityBack.Data;
using MetInProximityBack.Models;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.AspNetCore.TestHost;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using System;
using System.Collections.Generic;
using Microsoft.Data.Sqlite;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using System.Data.Common;
using Microsoft.EntityFrameworkCore.Infrastructure;

namespace MetInProximityBack.Tests
{
    public class CustomWebApplicationFactory<TProgram>
        : WebApplicationFactory<TProgram> where TProgram : class
    {
        protected override void ConfigureWebHost(IWebHostBuilder builder)
        {
            builder.ConfigureServices(services =>
            {
                var dbContextDescriptor = services.SingleOrDefault(
                d => d.ServiceType ==
                    typeof(DbContextOptionsBuilder<AppDbContext>));

                services.Remove(dbContextDescriptor);

                var dbConnectionDescriptor = services.SingleOrDefault(
                    d => d.ServiceType ==
                        typeof(DbConnection));

                services.Remove(dbConnectionDescriptor);

                // Create open SqliteConnection so EF won't automatically close it.
                services.AddSingleton<DbConnection>(container =>
                {
                    var connection = new SqliteConnection("DataSource=:memory:");
                    connection.Open();

                    return connection;
                });

                services.AddDbContext<AppDbContext>((container, options) =>
                {
                    var connection = container.GetRequiredService<DbConnection>();
                    options.UseSqlite(connection);
                });

                services.AddIdentity<AppUser, IdentityRole>()
                    .AddEntityFrameworkStores<AppDbContext>()
                    .AddDefaultTokenProviders();

                /*
                var cosmosDescriptor = services.SingleOrDefault(d => d.ServiceType == typeof(AzureCosmos));
                if (cosmosDescriptor != null) services.Remove(cosmosDescriptor);

                services.AddSingleton<AzureCosmos, TestAzureCosmos>();
                Dont need this until I send to production
                */

                using var scope = services.BuildServiceProvider().CreateScope();
                var dbContext = scope.ServiceProvider.GetRequiredService<AppDbContext>();
                dbContext.Database.EnsureCreated();

                builder.UseEnvironment("Development");

            });

            builder.ConfigureTestServices(services =>
            {
                services.AddAuthentication("TestAuth")
                    .AddScheme<AuthenticationSchemeOptions, TestAuthHandler>("TestAuth", options => { });
            });
        }    
    }
}
