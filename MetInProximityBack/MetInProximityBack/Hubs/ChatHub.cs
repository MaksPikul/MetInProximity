using MetInProximityBack.Constants;
using MetInProximityBack.Extensions;
using MetInProximityBack.Interfaces.IRepos;
using MetInProximityBack.Repositories;
using Microsoft.AspNetCore.SignalR;

namespace MetInProximityBack.Hubs
{
    public class ChatHub(
        ICacheRepo cache
    ) : Hub
    {
        private readonly ICacheRepo _cacheService = cache;

        public override async Task OnConnectedAsync()
        {
            try
            {
                var userId = Context.User.GetId();

                if (userId == null)
                {
                    await base.OnConnectedAsync();
                    return;
                }

                Console.WriteLine("Connected UserId: " + userId);  

                if (string.IsNullOrEmpty(userId))
                {
                    // Reject connection if userId is missing
                    Context.Abort();
                    return;
                }

                string connectionKey = AppConstants.ConnIdCacheKey(userId);
                Console.WriteLine("User ConnectionId: " + Context.ConnectionId);


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
            Console.WriteLine($"User Disconnected: {exception}");

            var userId = Context.User.GetId();
            Console.WriteLine("Disconnecting UserId: " + userId );

            if (userId == null)
            {
                await base.OnDisconnectedAsync(exception);
                return;
            }

            string connectionKey = AppConstants.ConnIdCacheKey(userId);
            string connectionId = await _cacheService.GetFromCacheAsync(connectionKey);

            _cacheService.RemoveFromCacheAsync(connectionKey);

            var connection = Context.ConnectionAborted;
            if (!connection.IsCancellationRequested)
            {
                Context.Abort(); // This will disconnect the client
            }

            await base.OnDisconnectedAsync(exception);
        }

    }
}
