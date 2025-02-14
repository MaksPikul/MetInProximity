namespace MetInProximityBack.Types.Message
{
    public class MessageResponse : MessageObject
    {
        public string UserId { get; set; } = Guid.NewGuid().ToString();
        public bool isPublic { get; set; }
        public string? RecipientId { get; set; } = string.Empty;
    }

}
