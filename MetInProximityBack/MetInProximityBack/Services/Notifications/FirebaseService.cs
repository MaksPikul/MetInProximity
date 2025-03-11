using Azure.Core;
using System.Net.Http.Headers;
using Microsoft.Azure.Cosmos.Serialization.HybridRow;
using System.Net;
using System.Net.Http;
using System.Text;
using MetInProximityBack.Types.Message;
using MetInProximityBack.Models;
using Microsoft.AspNetCore.Identity;
using MetInProximityBack.Interfaces.IServices;
//using Google.Apis.FirebaseCloudMessaging.v1;
//using Google.Apis.FirebaseCloudMessaging.v1.Data;
using Google.Apis.Auth.OAuth2;
using Google.Apis.Services;
using Microsoft.Extensions.Options;
using MetInProximityBack.Constants;
using System.Text.Json;
using Google.Apis;
using Microsoft.Azure.Cosmos;
using MetInProximityBack.Data;
using Microsoft.EntityFrameworkCore;
using FirebaseAdmin.Messaging;
using FirebaseAdmin;
using Microsoft.Identity.Client.Platforms.Features.DesktopOs.Kerberos;

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
            Console.WriteLine(fcmToken);
            if (fcmToken == null) {
                return;
            }

            var message = new Message
            {
                Token = fcmToken,
                /*
                Notification = new Notification
                {
                    Title = "Metin Message",
                    Body = "Check who sent you a Message !"
                },
                */
                Data = CreateFcmPayload(msgRes)
            };

            var response = await FirebaseMessaging.DefaultInstance.SendAsync(message);
                
            Console.WriteLine("went : " + response);
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