namespace MetInProximityBack.Interfaces.IServices
{
    public interface IMapService
    {
        Task<string> GetMapTiles(double lon, double lat, int zoom = 20);
    }
}
