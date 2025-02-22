using MetInProximityBack.Interfaces;
using StackExchange.Redis;
using System.Text.Json;

namespace MetInProximityBack.Services
{
    public class RedisCacheService : ICacheService
    {

        private readonly IDatabase _db;

        public RedisCacheService(IDatabase db)
        {
            _db = db;
        }

        public async Task AddToCacheAsync(string key, object obj)
        {
            string value = JsonSerializer.Serialize(obj);
            await _db.StringSetAsync(key, value);
        }

        public async Task<string> GetFromCacheAsync(string key)
        {
            RedisValue value =  await _db.StringGetAsync(key);
            return this.DeserializeRedisValue(value);
        }
        
        public async Task<List<string>> GetManyFromCacheAsync(List<string> keys)
        {
            RedisKey[] redisKeys = keys.ConvertAll(key => new RedisKey(key) ).ToArray();

            RedisValue[] values = await _db.StringGetAsync(redisKeys);
            Console.WriteLine(values[0].ToString()+ " Redis cache");

            List<string> deserializeValues = this.DeserializeRedisValues(values);

            return deserializeValues;
        }

        public void RemoveFromCacheAsync(string key)
        {
            _db.KeyDelete(key);
        }

        private string DeserializeRedisValue(RedisValue value)
        {
            if (value.IsNull)
            {
                return default;
            }
            // if not string, deserialise and turn json into string?

            return value.ToString();
        }

        private List<string> DeserializeRedisValues(RedisValue[] values)
        {
            List<string> deserializeValues = new List<string>();

            foreach (var value in values)
            {
                string deserializeValue = this.DeserializeRedisValue(value);
                deserializeValues.Add(deserializeValue);
            }

            return deserializeValues;
        }

    }
}
