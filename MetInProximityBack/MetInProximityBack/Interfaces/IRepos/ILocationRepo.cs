using MetInProximityBack.Types.Location;
using Microsoft.Azure.Cosmos.Spatial;

namespace MetInProximityBack.Interfaces.IRepos
{
    public interface ILocationRepo
    {
        Task AddLocation(LocationObject locObj);

        Task<List<NearbyUser>> GetNearbyLocations(Point contextPoint);

        Task<LocationObject> GetLocationObjectByUserId(string userId);

    }
}
