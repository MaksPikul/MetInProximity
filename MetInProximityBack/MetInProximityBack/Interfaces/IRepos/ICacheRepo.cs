using StackExchange.Redis;

namespace MetInProximityBack.Interfaces.IRepos
{
    public interface ICacheRepo
    {
        Task AddToCacheAsync(string key, string value);
        Task<string> GetFromCacheAsync(string key);
        Task<List<string>> GetManyFromCacheAsync(List<string> keys);
        void RemoveFromCacheAsync(string key);
    }
}
