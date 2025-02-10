using MetInProximityBack.Types.Message;

namespace MetInProximityBack.Interfaces
{
    public interface INotificationService
    {
        // userId is in msgRes, but i think this makes things clearer
        void SendPushNotification(string recipientId, MessageResponse msgRes);
    }
}
