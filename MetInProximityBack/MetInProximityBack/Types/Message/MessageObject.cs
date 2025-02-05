namespace MetInProximityBack.Types.Message
{
    public class MessageObject
    {
        public string Body { get; set; } = string.Empty;
        public DateTime Timestamp { get; set; } = DateTime.UtcNow;
    }
}
