using Newtonsoft.Json;

namespace MetInProximityBack.Types.Location
{
    public record GeoJsonPoint
    {

        [JsonProperty("type")]
        public string Type { get; set; } = "Point";

        [JsonProperty("coordinates")]
        public double[] Coordinates { get; set; }

        public GeoJsonPoint(double longitude, double latitude)
        {
            Coordinates = new double[] { longitude, latitude };
        }
    }
}
