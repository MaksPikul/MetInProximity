using MetInProximityBack.Interfaces.IRepos;
using StackExchange.Redis;
using System.Text.Json;

namespace MetInProximityBack.Repositories
{
    public class RedisCacheRepo : ICacheRepo
    {

        private readonly IDatabase _db;

        public RedisCacheRepo(IDatabase db)
        {
            _db = db;
        }

        public async Task AddToCacheAsync(string key, string value)
        {
            //string value = JsonSerializer.Serialize(obj);
            await _db.StringSetAsync(key, value);
        }

        public async Task<string> GetFromCacheAsync(string key)
        {
            RedisValue value = await _db.StringGetAsync(key);
            return this.DeserializeRedisValue(value);
        }

        public async Task<List<string>> GetManyFromCacheAsync(List<string> keys)
        {
            RedisKey[] redisKeys = keys.ConvertAll(key => new RedisKey(key)).ToArray();

            RedisValue[] values = await _db.StringGetAsync(redisKeys);

            List<string> deserializeValues = DeserializeRedisValues(values);

            return deserializeValues;
        }

        public void RemoveFromCacheAsync(string key)
        {
            _db.KeyDelete(key);
        }

        private List<string> DeserializeRedisValues(RedisValue[] values)
        {
            List<string> deserializeValues = new List<string>();

            foreach (var value in values)
            {
                this.DeserializeRedisValue(value);
            }

            return deserializeValues;
        }


        private string DeserializeRedisValue(RedisValue value)
        {
            if (value.IsNull)
            {
                return null;
            }
            else
            {
                return value.ToString();
            }
            // if not string, deserialise and turn json into string?
        }


    }
}
