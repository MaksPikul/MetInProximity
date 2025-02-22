using MetInProximityBack.Repositories;
using StackExchange.Redis;

namespace MetInProximityBack.Tests.ServiceTests
{
    // https://testcontainers.com/guides/getting-started-with-testcontainers-for-dotnet/
    // https://dotnet.testcontainers.org/

    // Integration Test For docker Redis Cache
    // Cant figure out TestContainers package right now
    public class RedisCacheTEST //: IAsyncLifetime 
    {

        private readonly RedisCacheRepo _redisCacheService;

        public RedisCacheTEST()
        {

            //var builder = new ContainerBuilder()
            //.ConfigureDatabaseConfiguration(6379);

            var multiplexer = ConnectionMultiplexer.Connect("host.docker.internal:6379");

            var db = multiplexer.GetDatabase();

            _redisCacheService = new RedisCacheRepo(db);
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

        [Fact] // Probably Shouldn't test all 3 methods in one, but to me this makes sense
        public async Task TEST_AddToCache_GetFromCache_RemoveFromCache()
        {
            var key = "testKey";
            var value = "Maks";

            await _redisCacheService.AddToCacheAsync(key, value);
            var cachedValue = await _redisCacheService.GetFromCacheAsync(key);

            Assert.NotNull(cachedValue);
            Assert.Equal("Maks", cachedValue);

            // ---

            _redisCacheService.RemoveFromCacheAsync(key);
            var removedValue = await _redisCacheService.GetFromCacheAsync(key);

            Assert.Null(removedValue);
        }

        // I want the list to include nulls
        [Fact]
        public async Task TEST_GetManyFromCacheAsync()
        {
            var keys = new List<string> { "key1", "key2", "key3" };
            var values = new List<string>
            {
                "val1",
                "val3"            
            };
            await _redisCacheService.AddToCacheAsync(keys[0], values[0]);
            await _redisCacheService.AddToCacheAsync(keys[2], values[1]);

            List<string> cachedValues = await _redisCacheService.GetManyFromCacheAsync(keys);

            Assert.NotNull(cachedValues);

            foreach (var value in cachedValues)
            {
                Console.WriteLine(value);
            }

            Assert.Equal(3, cachedValues.Count);

            Assert.Contains("val1", cachedValues);
            Assert.Contains("val3", cachedValues);
            Assert.Contains(null, cachedValues);
        }
    }
}
