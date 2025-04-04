using MetInProximityBack.Types.Message;
using MetInProximityBack.Types.NearbyUser;

namespace MetInProximityBack.Interfaces.IServices
{
    public interface INotificationService
    {
        Task RunPublicTasksAsync(MessageResponse msgRes, List<NearbyUserWithConnId> users);
        Task CreatePrivateTaskAsync(string recipientConnId, MessageResponse msgRes);
    }
}
