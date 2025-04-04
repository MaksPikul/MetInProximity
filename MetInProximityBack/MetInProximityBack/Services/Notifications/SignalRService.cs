using MetInProximityBack.Hubs;
using MetInProximityBack.Interfaces.IRepos;
using MetInProximityBack.Types.Message;
using Microsoft.AspNetCore.SignalR;
using Microsoft.Azure.Cosmos;

namespace MetInProximityBack.Services.Notifications
{
    public class SignalRService(
        IHubContext<ChatHub> hubContext
    ) : IWebSocketService
    {
        private readonly IHubContext<ChatHub> _hubContext = hubContext;

        public async Task SendNotification(string connectionId, MessageResponse msgRes)
        {
            await _hubContext.Clients.Client(connectionId).SendAsync("ReceiveMessage", msgRes);
        }
    }
}
