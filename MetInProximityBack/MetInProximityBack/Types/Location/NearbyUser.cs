using Newtonsoft.Json;

namespace MetInProximityBack.Types.Location
{
    public class NearbyUser
    {
        [JsonProperty("user_id")]
        public string UserId { get; set; }

        [JsonProperty("open_to_messages")]
        public bool openToMessages { get; set; } = false;
    }
}
