﻿using MetInProximityBack.Interfaces.IServices;
using MetInProximityBack.Services.Notifications;
using MetInProximityBack.Types.Message;
using MetInProximityBack.Types.NearbyUser;
using MetInProximityBack.Interfaces.IRepos;

namespace MetInProximityBack.Services
{
    public class NotificationService(
        IWebSocketService wsService,
        IPushNotifService pnService
    ) : INotificationService
    {
        private readonly IWebSocketService _srService = wsService;
        private readonly IPushNotifService _fbService = pnService;

        public async Task RunPublicTasksAsync(MessageResponse msgRes, List<NearbyUserWithConnId> users)
        {
            foreach (var user in users)
            {
                Console.WriteLine(user.UserId);
                await this.CreatePublicTask(user, msgRes);
            }
        }

        public async Task CreatePrivateTaskAsync(string recipientConnId, MessageResponse msgRes)
        {
            try 
            { 
                if (recipientConnId != null)
                {
                    await _srService.SendNotification(recipientConnId, msgRes);
                }
                else
                {
                    await _fbService.SendPushNotification(msgRes.RecipientId, msgRes);
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine("Failure to send message, Error :", ex.Message);
                throw ex;
            }
        }

        private async Task CreatePublicTask(NearbyUserWithConnId user, MessageResponse msgRes)
        {
            try
            { 
                if (user.connId != null && user.openToMessages)
                {
                    await _srService.SendNotification(user.connId, msgRes);
                }
                else if (user.openToMessages)
                {
                    await _fbService.SendPushNotification(user.UserId, msgRes);
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine("Failure to send message, Error :", ex.Message);
                throw ex;
            }
        }
    }
}