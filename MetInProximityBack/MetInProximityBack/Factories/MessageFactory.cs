using MetInProximityBack.Types.Message;

namespace MetInProximityBack.Factories
{
    public static class MessageFactory
    {
        public static MessageResponse CreateMessageResponse(
            MessageRequest msgReq,
            string SenderId,
            bool isPublic,
            string recipientId = null
        ) {
            return new MessageResponse
            {
                UserId = SenderId,
                Body = msgReq.Body,
                isPublic = isPublic,
                RecipientId = recipientId

            };
        }

    }
}
