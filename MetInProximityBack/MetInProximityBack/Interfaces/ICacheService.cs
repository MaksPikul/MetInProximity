using StackExchange.Redis;

namespace MetInProximityBack.Interfaces
{
    public interface ICacheService
    {
        Task AddToCacheAsync(string key, object value);
        Task<string> GetFromCacheAsync(string key);
        Task<List<string>> GetManyFromCacheAsync(List<string> keys);
        void RemoveFromCacheAsync(string key);
    }
}
