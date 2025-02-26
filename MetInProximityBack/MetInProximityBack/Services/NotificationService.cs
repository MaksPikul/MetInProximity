using MetInProximityBack.Interfaces.IServices;
using MetInProximityBack.Services.Notifications;
using MetInProximityBack.Types.Location;
using MetInProximityBack.Types.Message;
using Microsoft.AspNetCore.SignalR;
using Microsoft.Azure.Cosmos;

namespace MetInProximityBack.Services
{
    public class NotificationService(
        SignalRService signalRService,
        FirebaseService firebaseService
    ) : INotificationService
    {
        private readonly SignalRService _srService = signalRService;
        private readonly FirebaseService _fbService = firebaseService;

        public async Task RunPublicTasksAsync(MessageResponse msgRes, List<NearbyUserWithConnId> users)
        {
            var tasks = new List<Task>();

            foreach (var user in users)
            {
                tasks.Add(Task.Run(async () =>
                {
                    await this.CreatePublicTask(user.connId, msgRes, user.openToMessages);

                }));
            }

            await Task.WhenAll(tasks);
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
                Console.WriteLine("Failure to send message, Error :", ex);
            }
        }

        private async Task CreatePublicTask(string userConnectionId, MessageResponse msgRes, bool openToMessages)
        {
            try
            {
                if (userConnectionId != null && openToMessages)
                {
                    await _srService.SendNotification(userConnectionId, msgRes);
                }
                else if (openToMessages)
                {
                    await _fbService.SendPushNotification(msgRes.UserId, msgRes);
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine("Failure to send message, Error :", ex);
            }
        }
    }
}
