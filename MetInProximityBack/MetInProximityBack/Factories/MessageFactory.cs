﻿using MetInProximityBack.Types.Message;

namespace MetInProximityBack.Factories
{
    public static class MessageFactory
    {
        public static MessageResponse CreateMessageResponse(
            MessageRequest msgReq,
            string SenderId,
            bool isPublic
        ) {
            return new MessageResponse
            {
                UserId = SenderId,
                Body = msgReq.Body,
                Timestamp = msgReq.Timestamp,
                isPublic = isPublic
            };
        }

    }
}
