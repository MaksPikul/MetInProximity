﻿using MetInProximityBack.Types.Message;
//using Google.Apis.FirebaseCloudMessaging.v1;
//using Google.Apis.FirebaseCloudMessaging.v1.Data;
using MetInProximityBack.Data;
using Microsoft.EntityFrameworkCore;
using FirebaseAdmin.Messaging;

namespace MetInProximityBack.Services.Notifications
{
    public class FirebaseService(
        AppDbContext context
    ) {
        private readonly AppDbContext _context = context;

        // https://firebase.google.com/docs/cloud-messaging/send-message
        public async Task SendPushNotification(string recipientId, MessageResponse msgRes)
        {
            string fcmToken = await GetUserFcmToken(recipientId);

            if (fcmToken == null) {
                return;
            }

            var message = new Message
            {
                Token = fcmToken,
                Data = CreateFcmPayload(msgRes)
            };

            var response = await FirebaseMessaging.DefaultInstance.SendAsync(message);
        }
        private Dictionary<string, string> CreateFcmPayload(MessageResponse msgRes)
        {
            return new Dictionary<string, string>
            {
                { "UserId", msgRes.UserId },
                { "isPublic", msgRes.isPublic.ToString() },
                { "Body",  msgRes.Body },
                { "RecipientId", msgRes.RecipientId},
                { "Timestamp", msgRes.Timestamp.ToString() },
            };
        }

        // Need to handle nulls
        private async Task<string?> GetUserFcmToken(string userId)
        {
            return await _context.Users
                .Where(u => u.Id == userId)
                .Select(u => u.FcmToken)
                .FirstOrDefaultAsync();
        }
    }
}