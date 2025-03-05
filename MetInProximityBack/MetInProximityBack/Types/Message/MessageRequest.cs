using MetInProximityBack.Enums;

namespace MetInProximityBack.Types.Message
{
    public class MessageRequest : MessageObject
    {
        public double lon { get; set; }
        public double lat { get; set; }
        //public MessageAccess access;
    }
}
