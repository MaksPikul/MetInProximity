using System.ComponentModel.DataAnnotations;

namespace MetInProximityBack.Types.Message
{
    public class PrivateMessageRequest : MessageRequest
    {
        [Required]
        public string MsgRecipientId { get; set; } = string.Empty;
    }
}
