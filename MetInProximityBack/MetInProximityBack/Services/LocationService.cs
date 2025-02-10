using MetInProximityBack.Constants;
using MetInProximityBack.Data;
using MetInProximityBack.Interfaces;
using MetInProximityBack.Types.Location;

namespace MetInProximityBack.Services
{
    public class LocationService (
        INoSqlDb cosmosDb,
        ICacheService cacheService
    ) 
    {
        private readonly INoSqlDb _cosmosDb = cosmosDb;
        private readonly ICacheService _cacheService = cacheService;

        public async Task<List<NearbyUser>> GetNearbyUsersAsync(double longitude, double latitude)
        {
            // UserId : ConnectionString (SignalR)
            // List of Connection Ids
            List<NearbyUser> nearbyUsers = await _cosmosDb
                    .GetNearbyLocations(
                        LocationFactory.CreatePoint(longitude, latitude)
                    );

            return nearbyUsers;
        }

        public async Task<List<NearbyUserWithConnId>> GetUserConnIdsAsync(List<NearbyUser> nearbyUsers)
        {
            List<string> connectionIds = await _cacheService
                    .GetManyFromCacheAsync<string>(
                        nearbyUsers
                        .Select(user => CacheKeys.ConnIdCacheKey(user.UserId))
                        .ToList()
                    );

            List<NearbyUserWithConnId> nuwConnId = this.MapUserToConnId(nearbyUsers, connectionIds);
            return nuwConnId;
        }

        private List<NearbyUserWithConnId> MapUserToConnId(List<NearbyUser> nearbyUsers, List<string> connectionIds)
        {
            var result = new List<NearbyUserWithConnId>();

            for (int i = 0; i < nearbyUsers.Count; i++)
            {
                result.Add(
                    new NearbyUserWithConnId(nearbyUsers[i], connectionIds[i])
                );
            }

            return result;
        }
    }
}
