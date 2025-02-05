using MetInProximityBack.Enums;
using MetInProximityBack.Interfaces;
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
        INoSqlDb cosmosDb

    ) : Controller
    {

        private readonly INoSqlDb _cosmosDb = cosmosDb;

        [HttpPost("{method}")]
        public async Task<IActionResult> ReceiveMessageAndNotify(
            [FromBody] MessageRequest msgReq
        ) {

            // Private or Public
            // If public 

            List<LocationObject> nearbyUserIds = await _cosmosDb
                .GetNearbyLocations(
                    LocationFactory.CreatePoint( msgReq.Longitude, msgReq.Latitude )
                );

            // Get redis/signal r connected users which are on the list
            // Check if they want messages
            // send accordingly,

            // Do the same for those not connected by signalR/Redis
            // They will Need to be able to see the message once it loads

            return View();
        }
    }
}
