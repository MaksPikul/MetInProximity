using MetInProximityBack.Types.Location;
using MetInProximityBack.Types.NearbyUser;

namespace MetInProximityBack.Interfaces.IServices
{
    public interface IMessageService
    {
        Task<List<NearbyUser>> GetNearbyUsersAsync(double longitude, double latitude, string RequestingUserId);
        Task<string> GetConnectionIdAsync(string recipientId);
        Task<List<NearbyUserWithConnId>> GetConnectionIdsAsync(List<NearbyUser> nearbyUsers);
        Task<LocationObject?> GetLatestLocationAsync(string userId);
        Task<LocationObject> UpdateLocation(LocationObject locationObject, string propertyName, object newVal);
    }
}
