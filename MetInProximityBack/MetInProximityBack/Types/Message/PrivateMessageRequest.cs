namespace MetInProximityBack.Types.Message
{
    public class PrivateMessageRequest : MessageRequest
    {
        public string MsgRecipientId { get; set; } = String.Empty;
    }
}
