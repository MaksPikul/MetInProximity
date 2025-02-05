namespace MetInProximityBack.Types.Message
{
    public class MessageResponse : MessageObject
    {
        public string UserId { get; set; } = Guid.NewGuid().ToString();
    }

}
