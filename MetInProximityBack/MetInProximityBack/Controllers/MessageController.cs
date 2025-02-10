using MetInProximityBack.Interfaces;
using MetInProximityBack.Enums;
using MetInProximityBack.Models;
using MetInProximityBack.Types.Location;
using MetInProximityBack.Types.Message;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using MetInProximityBack.Factories;
using MetInProximityBack.Extensions;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.SignalR;
using MetInProximityBack.Hubs;
using System.Threading.Tasks;
using Microsoft.Azure.Cosmos;
using System.Collections.Generic;
using MetInProximityBack.Constants;
using MetInProximityBack.Services;

namespace MetInProximityBack.Controllers
{
    [Route("api/message")]
    [ApiController]
    public class MessageController(
        ICacheService cacheService,
        IHubContext<ChatHub> hubContext,
        INotificationService notifService,
        LocationService locService

    ) : Controller
    {
        private readonly ICacheService _cacheService = cacheService;
        private readonly IHubContext<ChatHub> _hubContext = hubContext;
        private readonly INotificationService _notifService = notifService;
        private readonly LocationService _locService = locService;

        [HttpPost]
        [Authorize]
        public async Task<IActionResult> PublicReceiveMessageAndNotify(
            [FromBody] MessageRequest msgReq
        ) {
            try {

                List<NearbyUser> nearbyUsers = await _locService.GetNearbyUsersAsync(msgReq.Longitude, msgReq.Latitude);

                List<NearbyUserWithConnId> nuwConnId = await _locService.GetUserConnIdsAsync(nearbyUsers);

                MessageResponse msgRes = MessageFactory.CreateMessageResponse(msgReq, User.GetId());

                List<Task> tasks = this.CreateMsgTasksForParallel(msgRes, nuwConnId);

                await Task.WhenAll(tasks);

                return Ok("Message Sent");
            }
            catch (Exception ex) {
                return StatusCode(500, "Failed to send message: " + ex.Message);
            }
        }

        [HttpPost]
        [Authorize]
        public async Task<IActionResult> PrivateReceiveMessageAndNotify(
            [FromBody] PrivateMessageRequest msgReq
        )
        {
            try
            {
                string recipientConnId = await _cacheService
                    .GetFromCacheAsync( CacheKeys.ConnIdCacheKey( msgReq.MsgRecipientId) );

                MessageResponse msgRes = MessageFactory.CreateMessageResponse(msgReq, User.GetId());

                if (recipientConnId != null)
                {
                    await _hubContext.Clients.Client(recipientConnId).SendAsync("ReceivePrivateMessage", msgRes, msgReq.MsgRecipientId);
                }
                else
                {
                    _notifService.SendPushNotification(msgReq.MsgRecipientId, msgRes);
                }

                return Ok("Message Sent");
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Failed to send message: " + ex.Message);
            }
        }

        private List<Task> CreateMsgTasksForParallel(MessageResponse msgRes, List<NearbyUserWithConnId> users)
        {
            var tasks = new List<Task>();

            // Managed to make this process O(n) instead of O(n^2)
            // Wanted to use HashSets, but this was easier
            foreach (var user in users)
            {
                tasks.Add(Task.Run(async () =>
                {
                    try
                    {
                        if (user.connId != null && user.openToMessages)
                        {
                            await _hubContext.Clients.Client(user.connId).SendAsync("ReceivePublicMessage", msgRes);
                        }
                        else if (user.openToMessages)
                        {
                            _notifService.SendPushNotification(user.UserId, msgRes);
                        }
                    }
                    catch (Exception ex)
                    {
                        Console.WriteLine("Failure to send message, Error :", ex);
                    }
                }));

            }
            return tasks;
        }


    }
}

/*  Unnecessary, leaving this to display time complexity saved
             
    List<string> offlineUsers = nearbyUsers
        .Where(nuser => !socketConnectedUsers.Contains(nuser.UserId))
        .Select(user => user.UserId)
        .ToList();

*/