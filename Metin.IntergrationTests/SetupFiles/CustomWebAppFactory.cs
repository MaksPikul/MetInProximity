using Docker.DotNet.Models;
using MetInProximityBack.Data;
using MetInProximityBack.Tests.SetupFiles;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.AspNetCore.SignalR.Client;
using Microsoft.Data.SqlClient;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using Respawn;
using RTools_NTS.Util;
using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Common;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Testcontainers.CosmosDb;
using Testcontainers.Redis;
using Testcontainers.SqlEdge;


/*
 * https://antondevtips.com/blog/asp-net-core-integration-testing-best-practises
 */

namespace Metin.IntergrationTests.SetupFiles
{
    public class CustomWebAppFactory : WebApplicationFactory<Program>, IAsyncLifetime
    {
        private DbConnection _dbConnection = null!;
        

        private Respawner _respawner = null!;

        private readonly Lazy<RedisContainer> _redisContainer = new(() => new RedisBuilder().Build());
        private readonly Lazy<CosmosDbContainer> _cosmosContainer = new(() => new CosmosDbBuilder().Build());
        private readonly Lazy<SqlEdgeContainer> _sqlEdgeContainer = new(() => new SqlEdgeBuilder().Build());

        public HttpClient HttpClient { get; private set; } = null!;
        public HubConnection hubConnection { get; private set; } = null!;

        public async Task InitializeAsync()
        {

            await _redisContainer.Value.StartAsync();
            await _cosmosContainer.Value.StartAsync();
            await _sqlEdgeContainer.Value.StartAsync();

            HttpClient = CreateClient();
            string dogUser = "Bearer " + Constants.DOG_TEST_JWT;
            string catUser = "Bearer " + Constants.CAT_TEST_JWT;

            hubConnection = new HubConnectionBuilder()
                .WithUrl($"{HttpClient.BaseAddress}chathub", options =>
                {
                    options.Headers["Authorization"] = dogUser; 
                })
                .WithAutomaticReconnect()
                .Build();
          
            HttpClient.DefaultRequestHeaders.Add("Authorization", catUser);

            _dbConnection = new SqlConnection(_sqlEdgeContainer.Value.GetConnectionString());  

            await _dbConnection.OpenAsync();
            await InitializeRespawnerAsync();
        }

        public new async Task DisposeAsync()
        {
            await _redisContainer.Value.DisposeAsync();
            await _cosmosContainer.Value.DisposeAsync();
            await _sqlEdgeContainer.Value.DisposeAsync();
            await base.DisposeAsync();
        }

        private async Task InitializeRespawnerAsync()
        {
            _respawner = await Respawner.CreateAsync(_dbConnection, new RespawnerOptions
            {
                SchemasToInclude = ["Metin"],
                DbAdapter = DbAdapter.SqlServer
            });
        }

        public async Task ResetDatabaseAsync()
        {
            await _respawner.ResetAsync(_dbConnection);
        }

        protected override void ConfigureWebHost(IWebHostBuilder builder)
        {

            Environment.SetEnvironmentVariable("ConnectionStrings:DefaultConnection", _sqlEdgeContainer.Value.GetConnectionString());
            Environment.SetEnvironmentVariable("ConnectionStrings:RedisConnectionString", _redisContainer.Value.GetConnectionString());
            Environment.SetEnvironmentVariable("ConnectionStrings:CosmosAccountEndpoint", _cosmosContainer.Value.GetConnectionString());

            builder.ConfigureServices(services =>
            {
                var descriptor = services.SingleOrDefault(s => s.ServiceType == typeof(DbContextOptions<AppDbContext>));
                if (descriptor != null)
                {
                    services.Remove(descriptor);
                }

                var dbConnectionDescriptor = services.SingleOrDefault(s => s.ServiceType == typeof(DbConnection));
                if (dbConnectionDescriptor != null)
                {
                    services.Remove(dbConnectionDescriptor);
                }

                services.AddDbContext<AppDbContext>(options =>
                {
                    options.UseSqlServer(_sqlEdgeContainer.Value.GetConnectionString());
                });
            });
        }
    }
}
