using MetInProximityBack.Constants;
using MetInProximityBack.Data;
using MetInProximityBack.Interfaces;
using MetInProximityBack.Types.Location;
using Microsoft.EntityFrameworkCore.Metadata.Internal;
using System.Reflection;

namespace MetInProximityBack.Services
{
    public class LocationService (
        AzureCosmos cosmosDb,
        ICacheService cacheService
    ) 
    {
        private readonly AzureCosmos _cosmosDb = cosmosDb;
        private readonly ICacheService _cacheService = cacheService;

        public async Task<List<NearbyUser>> GetNearbyUsersAsync(double longitude, double latitude, string RequestingUserId)
        {
            // UserId : ConnectionString (SignalR)
            // List of Connection Ids
            List<NearbyUser> nearbyUsers = await _cosmosDb
                    .GetNearbyLocations(
                        LocationFactory.CreatePoint(longitude, latitude),
                        RequestingUserId
                    );

            return nearbyUsers;
        }

        public async Task<List<NearbyUserWithConnId>> GetUserConnIdsAsync(List<NearbyUser> nearbyUsers)
        {
            List<string> connectionIds = await _cacheService
                    .GetManyFromCacheAsync(
                        nearbyUsers
                        .Select(user => CacheKeys.ConnIdCacheKey(user.UserId))
                        .ToList()
                    );
            Console.WriteLine(connectionIds[0] + "LocService");

            List<NearbyUserWithConnId> nuwConnId = this.MapUserToConnId(nearbyUsers, connectionIds);
            return nuwConnId;
        }

        // get by any field
        public async Task<LocationObject> GetLatestLocationAsync(string userId)
        {
            LocationObject locationObject = await _cosmosDb.GetLocationObjectByUserId(userId);
            return locationObject;
        }

        public async void UpdateLocation(LocationObject locationObject, string propertyName, object newVal)
        {

            PropertyInfo property = typeof(LocationObject).GetProperty(propertyName);

            if ((property != null && property.CanWrite) && property.PropertyType == newVal.GetType())
            {
                property.SetValue(locationObject, newVal);
                await _cosmosDb.AddLocation(locationObject);
            }
            else
            {
                throw new Exception("Property doesn't exist or cannot be changed."); 
            }
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
