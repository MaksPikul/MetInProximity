using MetInProximityBack.Services;
using StackExchange.Redis;

namespace MetInProximityBack.Tests.ServiceTests
{
    // https://testcontainers.com/guides/getting-started-with-testcontainers-for-dotnet/
    // https://dotnet.testcontainers.org/

    // Integration Test For docker Redis Cache
    // Cant figure out TestContainers package right now
    public class RedisCacheTEST //: IAsyncLifetime 
    {

        private readonly RedisCacheService _redisCacheService;

        public RedisCacheTEST() {

            //var builder = new ContainerBuilder()
            //.ConfigureDatabaseConfiguration(6379);

            var multiplexer = ConnectionMultiplexer.Connect("host.docker.internal:6379");

            var db = multiplexer.GetDatabase();

            _redisCacheService = new RedisCacheService(db);
        }

        // IAsyncLifetime Methods which will spin up the docker container
        // before tests i believe
        /*
        public async Task InitializeAsync()
        {
            //await _redisContainer.StartAsync();
        }

        public async Task DisposeAsync()
        {
            //await _redisContainer.StopAsync();
        }
        */

    }
}
