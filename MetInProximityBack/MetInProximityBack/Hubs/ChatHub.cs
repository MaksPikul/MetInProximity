using MetInProximityBack.Constants;
using MetInProximityBack.Interfaces;
using Microsoft.AspNetCore.SignalR;

namespace MetInProximityBack.Hubs
{
    public class ChatHub(
        ICacheService cache
        ) : Hub
    {
        private readonly ICacheService _cacheService;

        public override async Task OnConnectedAsync()
        {
            var httpContext = Context.GetHttpContext();
            var userId = httpContext.Request.Query["userId"];

            string connectionKey = CacheKeys.ConnIdCacheKey(userId);
            _cacheService.AddToCacheAsync(connectionKey, Context.ConnectionId);

            base.OnConnectedAsync();
        }

        public override async Task OnDisconnectedAsync(Exception? exception)
        {
            var httpContext = Context.GetHttpContext();
            var userId = httpContext.Request.Query["userId"];

            string connectionKey = CacheKeys.ConnIdCacheKey(userId);
            string connectionId = await _cacheService.GetFromCacheAsync(connectionKey);

            _cacheService
                .RemoveFromCacheAsync(connectionKey);

            var connection = Context.ConnectionAborted;
            if (!connection.IsCancellationRequested)
            {
                Context.Abort(); // This will disconnect the client
            }

            base.OnDisconnectedAsync(exception);
        }

    }
}
