using StackExchange.Redis;

namespace MetInProximityBack.Interfaces
{
    public interface ICacheService
    {
        Task AddToCacheAsync(string key, object value);
        Task<string> GetFromCacheAsync(string key);
        Task<List<T>> GetManyFromCacheAsync<T>(List<string> keys);
        Task RemoveFromCacheAsync(string key);
    }
}
