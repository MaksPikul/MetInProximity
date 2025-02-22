using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http.Headers;
using System.Text;
using System.Threading.Tasks;
using MetInProximityBack.Controllers


using MetInProximityBack.Models;
using Microsoft.AspNetCore.Identity;
using Microsoft.Azure.Cosmos;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Options;

namespace MetInProximityBack.Tests.ControllerTests
{
    public class LocationControllerTEST :
        IClassFixture<CustomWebApplicationFactory<Program>>
    {
        private readonly HttpClient _client;
        private readonly IServiceScopeFactory _scopeFactory;

        public LocationControllerTEST(CustomWebApplicationFactory<Program> factory)
        {
            _client = factory.CreateClient();
            _client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", "test-jwt-token");
            _scopeFactory = factory.Services.GetRequiredService<IServiceScopeFactory>();
        }

        private async Task<AppUser> CreateTestUser()
        {
            using var scope = _scopeFactory.CreateScope();
            var userManager = scope.ServiceProvider.GetRequiredService<UserManager<AppUser>>();

            var user = new AppUser { UserName = "testuser", Email = "test@example.com" };
            await userManager.CreateAsync(user, "Test@1234");
            return user;
        }

       




    }
}
