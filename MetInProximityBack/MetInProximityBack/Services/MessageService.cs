using MetInProximityBack.Constants;
using MetInProximityBack.Hubs;
using MetInProximityBack.Interfaces.IRepos;
using MetInProximityBack.Interfaces.IServices;
using MetInProximityBack.Repositories;
using MetInProximityBack.Types.Location;
using MetInProximityBack.Types.Message;
using Microsoft.AspNetCore.SignalR;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata.Internal;
using System.Reflection;

namespace MetInProximityBack.Services
{
    // This Class handles User Locations and Connection IDs,
    // This class is necessary for message controller to work, hence why i chose this name,
    // Notification Service actually sends out the messages,
    // LocationAndConnectionIdService would not be a good name
    public class MessageService (
        CosmoLocationRepo cosmosDb,
        RedisCacheRepo cacheService
    ) : IMessageService
    {

        private readonly CosmoLocationRepo _cosmosDb = cosmosDb;
        private readonly RedisCacheRepo _cacheService = cacheService;

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

        public async Task<string> GetConnectionIdAsync(string recipientId)
        {
            return await _cacheService.GetFromCacheAsync(AppConstants.ConnIdCacheKey(recipientId));
        }

        public async Task<List<NearbyUserWithConnId>> GetConnectionIdsAsync(List<NearbyUser> nearbyUsers)
        {
            List<string> connectionIds = await _cacheService
                    .GetManyFromCacheAsync(
                        nearbyUsers
                        .Select(user => AppConstants.ConnIdCacheKey(user.UserId))
                        .ToList()
                    );

            List<NearbyUserWithConnId> nuwConnId = this.MapUserToConnId(nearbyUsers, connectionIds);
            return nuwConnId;
        }

        // get by any field?
        public async Task<LocationObject?> GetLatestLocationAsync(string userId)
        {
            LocationObject? locationObject = await _cosmosDb.GetLocationByUserId(userId);
            return locationObject;
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

        // Trying something out
        public async Task<LocationObject> UpdateLocation(LocationObject locationObject, string propertyName, object newVal)
        {
            PropertyInfo? property = typeof(LocationObject).GetProperty(propertyName);

            if ((property != null && property.CanWrite) && property.PropertyType == newVal.GetType())
            {
                property.SetValue(locationObject, newVal);
                await _cosmosDb.AddOrUpdateLocation(locationObject);
                return locationObject;
            }
            else
            {
                throw new Exception("Property doesn't exist or cannot be changed.");
            }
        }
    }
}
