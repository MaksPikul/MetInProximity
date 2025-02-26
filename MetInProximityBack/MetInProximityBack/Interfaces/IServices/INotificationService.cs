using MetInProximityBack.Types.Location;
using MetInProximityBack.Types.Message;

namespace MetInProximityBack.Interfaces.IServices
{
    public interface INotificationService
    {
        Task RunPublicTasksAsync(MessageResponse msgRes, List<NearbyUserWithConnId> users);
        Task CreatePrivateTaskAsync(string recipientConnId, MessageResponse msgRes);
    }
}
