using MetInProximityBack.Types.Location;

namespace MetInProximityBack.Interfaces
{
    public interface INoSqlDb
    {
        Task AddLocation(LocationObject locObj);


    }
}
