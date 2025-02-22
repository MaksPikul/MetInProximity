using MetInProximityBack.Enums;

namespace MetInProximityBack.Types.Message
{
    public class MessageRequest : MessageObject
    {
        public double Longitude { get; set; }
        public double Latitude { get; set; }
        //public MessageAccess access;
    }
}
