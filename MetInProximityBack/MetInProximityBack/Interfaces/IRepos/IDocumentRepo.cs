
using Microsoft.Azure.Cosmos.Spatial;
using MetInProximityBack.Types.Location;
using MetInProximityBack.Types.NearbyUser;
using Microsoft.Azure.Cosmos;

namespace MetInProximityBack.Interfaces.IRepos
{
    public interface IDocumentRepo
    {
        Task<ItemResponse<LocationObject>> AddOrUpdateLocation(LocationObject locObj);
        Task<ItemResponse<LocationObject>> RemoveLocation(LocationObject locObj);

        Task<List<NearbyUser>> GetNearbyLocations(Point contextPoint, string RequestingUserId);
        Task<LocationObject?> GetLocationByUserId(string userId);
    }
}
