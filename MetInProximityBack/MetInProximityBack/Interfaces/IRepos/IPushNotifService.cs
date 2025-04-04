using MetInProximityBack.Types.Message;

namespace MetInProximityBack.Interfaces.IRepos
{
    public interface IPushNotifService
    {
        Task SendPushNotification(string recipientId, MessageResponse msgRes);
    }
}
