using Microsoft.Azure.Cosmos.Spatial;
using Newtonsoft.Json;

namespace MetInProximityBack.Types.Location
{
    public record LocationObject
    {
        [JsonProperty("id")]
        public string Id { get; set; } = Guid.NewGuid().ToString();

        [JsonProperty("user_id")]
        public string UserId { get; set; }

        [JsonProperty("timestamp")]
        public DateTime Timestamp { get; set; } = DateTime.UtcNow;

        [JsonProperty("location")]
        public Point Location { get; set; }

        [JsonProperty("geohash")]
        public string Geohash { get; set; }

        [JsonProperty("open_to_messages")]
        public bool openToMessages { get; set; } = false;


    }
}
