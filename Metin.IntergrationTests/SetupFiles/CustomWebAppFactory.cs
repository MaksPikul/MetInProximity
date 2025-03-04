using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc.Testing;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Testcontainers.CosmosDb;
using Testcontainers.Redis;
using Testcontainers.SqlEdge;

namespace Metin.IntergrationTests.SetupFiles
{
    public class CustomWebAppFactory : WebApplicationFactory<Program>, IAsyncLifetime
    {



        private readonly RedisContainer _redisContainer = new RedisBuilder()
            .Build();

        private readonly CosmosDbContainer _cosmosContainer = new CosmosDbBuilder()
            .Build();

        private readonly SqlEdgeContainer _sqlEdgeContainer = new SqlEdgeBuilder()
            .Build();

        public HttpClient HttpClient { get; private set; } = null!;

        public async Task InitializeAsync()
        {

            await _redisContainer.StartAsync();
            await _cosmosContainer.StartAsync();
            await


        }

        public new async Task DisposeAsync()
        {
        }

        protected override void ConfigureWebHost(IWebHostBuilder builder)
        {
        }
    }
}
