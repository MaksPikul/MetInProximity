using MetInProximityBack.Interfaces;
using MetInProximityBack.Enums;
using MetInProximityBack.Models;
using MetInProximityBack.Types.Location;
using MetInProximityBack.Types.Message;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;

namespace MetInProximityBack.Controllers
{
    [Route("api/message")]
    [ApiController]
    public class MessageController(
        INoSqlDb cosmosDb,
        ICacheService cacheService
    ) : Controller
    {

        private readonly INoSqlDb _cosmosDb = cosmosDb;
        private readonly ICacheService _cacheService = cacheService;

        [HttpPost]
        public async Task<IActionResult> ReceiveMessageAndNotify(
            [FromBody] MessageRequest msgReq
        ) {

            HashSet<NearbyUser> nearbyUsers = await _cosmosDb
                .GetNearbyLocations(
                    LocationFactory.CreatePoint( msgReq.Longitude, msgReq.Latitude )
                );

            // UserId : ConnectionString (SignalR)
            List<string> socketConnectedUsers = await _cacheService
                .GetManyFromCache<string>(
                    nearbyUsers.Select(user => user.UserId).ToList()
                );

            // Managed to make this process O(n) instead of O(n^2)
            var tasks = new List<Task>();

            foreach (var nearbyUser in nearbyUsers)
            {
                tasks.Add(Task.Run(async () =>
                {
                    try
                    {
                        if (socketConnectedUsers.Contains(nearbyUser.UserId) && nearbyUser.openToMessages)
                        {
                            // Signal R Send message Logic
                        }
                        else if (nearbyUser.openToMessages)
                        {
                            // Firebase Send Message Logic
                        }
                    }
                    catch (Exception ex)
                    {
                        Console.WriteLine("Failure to send message, Error :", ex);
                    }
                }));
            }
            await Task.WhenAll(tasks);

            return Ok("Message sent");
        }
    }
}






/*  Unnecessary, leaving this to display time complexity saved
             
    List<string> offlineUsers = nearbyUsers
        .Where(nuser => !socketConnectedUsers.Contains(nuser.UserId))
        .Select(user => user.UserId)
        .ToList();

*/