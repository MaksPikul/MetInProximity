using MetInProximityBack.Interfaces;
using Microsoft.Extensions.Caching.Distributed;
using Newtonsoft.Json.Linq;
using StackExchange.Redis;
using System.Text.Json;

namespace MetInProximityBack.Services
{
    public class RedisCacheService( 
        IDistributedCache cache,
        IDatabase db
        ) : ICacheService
    {
        private readonly IDistributedCache _dCache = cache;
        private readonly IDatabase _db = db;

        public async Task AddToCacheAsync(string key, object obj)
        {
            string value = JsonSerializer.Serialize(obj);
            _dCache.SetStringAsync(key, value);
        }
        public async Task<string> GetFromCacheAsync(string key)
        {
            return await _dCache.GetStringAsync(key);
        }
        
        public async Task<List<T>> GetManyFromCache<T>(List<string> keys)
        {
            RedisKey[] redisKeys = keys.ConvertAll(key => new RedisKey(key) ).ToArray();

            RedisValue[] values = await db.StringGetAsync(redisKeys);

            List<T> deserializeValues = this.DeserializeRedisValues<T>(values);

           return deserializeValues;
        }

        public async Task RemoveFromCacheAsync(string key)
        {
            _dCache.Remove(key);
        }

        private T DeserializeRedisValue<T>(RedisValue value)
        {
            T obj = JsonSerializer.Deserialize<T>(value);

            return obj;
        }

        private List<T> DeserializeRedisValues<T>(RedisValue[] values)
        {
            List<T> deserializeValues = new List<T>();

            foreach (var value in values)
            {
                T deserializeValue = this.DeserializeRedisValue<T>(value);
                deserializeValues.Add(deserializeValue);
            }

            return deserializeValues;
        }

    }
}
