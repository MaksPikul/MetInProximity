using MetInProximityBack.Interfaces.IServices;
using MetInProximityBack.Types.Location;
using Microsoft.AspNetCore.Mvc;
using System.ComponentModel;
using System.Drawing;
using static NetTopologySuite.Geometries.Utilities.GeometryMapper;
using System.Security.Policy;
using static System.Net.WebRequestMethods;

namespace MetInProximityBack.Services
{
    public class MapService(
        IHttpClientFactory httpClientFactory,
        IConfiguration config
    ) : IMapService
    {

        private readonly IHttpClientFactory _httpClientFactory = httpClientFactory;
        private readonly IConfiguration _config = config;

        public async Task<string> GetMapTiles(
            double lon,
            double lat,
            int zoom = 20
        ) {
            var bbox = CalculateBoundingBox(lat, lon, 500);

            //.comic, 2x.png
            string mapboxUrl = $"https://api.mapbox.com/styles/v1/mapbox/satellite-streets-v12/static/{bbox}/600x1200?access_token={_config["MapBox:ApiKey"]}";

            HttpClient httpClient = _httpClientFactory.CreateClient();
            byte[] mapImageData = await httpClient.GetByteArrayAsync(mapboxUrl);

            string mapImageBase64 = Convert.ToBase64String(mapImageData);

            return mapImageBase64;
        }

        // mapBox Docs LLM, theres an sdk for this on android, want to do this here tho
        private string CalculateBoundingBox(double lat, double lon, double distanceMeters)
        {
            const double R = 6378137;

            double dlat = distanceMeters / R;
            double dlon = distanceMeters / (R * Math.Cos(Math.PI * lat / 180));

            double minLat = lat - dlat * 180 / Math.PI;
            double maxLat = lat + dlat * 180 / Math.PI;
            double minLon = lon - dlon * 180 / Math.PI;
            double maxLon = lon + dlon * 180 / Math.PI;

            return $"[{minLon},{minLat},{maxLon},{maxLat}]";
        }
    }
}
