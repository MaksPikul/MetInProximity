using Microsoft.Azure.Cosmos;

namespace MetInProximityBack.Constants
{
    public static class CacheKeys
    {
        public static string ConnIdCacheKey(string userId)
        {
            return $"CHAT/USER:{userId}";
        }



    }
}
