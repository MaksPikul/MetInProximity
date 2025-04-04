using MetInProximityBack.Types.Message;

namespace MetInProximityBack.Interfaces.IRepos
{
    public interface IWebSocketService
    {
        Task SendNotification(string connectionId, MessageResponse msgRes);
    }
}
