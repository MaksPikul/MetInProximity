using MetInProximityBack.Constants;
using MetInProximityBack.Extensions;
using MetInProximityBack.Interfaces;
using Microsoft.AspNetCore.SignalR;

namespace MetInProximityBack.Hubs
{
    public class ChatHub(
        ICacheService cache
        ) : Hub
    {
        private readonly ICacheService _cacheService = cache;

        public override async Task OnConnectedAsync()
        {
            try
            {
                var userId = Context.User.GetId();

                Console.WriteLine(userId);
                Console.WriteLine(Context.ConnectionId.ToString());

                if (string.IsNullOrEmpty(userId))
                {
                    // Reject connection if userId is missing
                    Context.Abort();
                    return;
                }

                string connectionKey = CacheKeys.ConnIdCacheKey(userId);
                
                await _cacheService.AddToCacheAsync(connectionKey, Context.ConnectionId);

                await base.OnConnectedAsync();
            }
            catch (HubException ex){
                throw;
            }
            catch (Exception ex)
            {
                throw new HubException("Failure outside of hub: " + ex.Message);
            }
        }

        public override async Task OnDisconnectedAsync(Exception? exception)
        {
            var userId = Context.User.GetId();

            string connectionKey = CacheKeys.ConnIdCacheKey(userId);
            string connectionId = await _cacheService.GetFromCacheAsync(connectionKey);

            await _cacheService
                .RemoveFromCacheAsync(connectionKey);

            var connection = Context.ConnectionAborted;
            if (!connection.IsCancellationRequested)
            {
                Context.Abort(); // This will disconnect the client
            }

            await base.OnDisconnectedAsync(exception);
        }

    }
}
