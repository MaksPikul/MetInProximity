using MetInProximityBack.Types.Location;
using Microsoft.Azure.Cosmos.Spatial;

namespace MetInProximityBack.Interfaces
{
    public interface INoSqlDb
    {
        Task AddLocation(LocationObject locObj);

        Task<List<NearbyUser>> GetNearbyLocations(Point contextPoint);

        Task<LocationObject> GetLocationObjectByUserId(string userId);

    }
}
