using Newtonsoft.Json;

namespace MetInProximityBack.Types.NearbyUser
{
    public class NearbyUser
    {
        [JsonProperty("user_id")]
        public string UserId { get; set; } = string.Empty;

        [JsonProperty("open_to_messages")]
        public bool openToMessages { get; set; } = false;

        [JsonProperty("open_to_private")]
        public bool openToPrivate { get; set; } = false;
    }
}
