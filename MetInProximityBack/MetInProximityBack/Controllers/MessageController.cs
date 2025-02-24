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
using MetInProximityBack.Interfaces.IRepos;
using MetInProximityBack.Interfaces.IServices;

namespace MetInProximityBack.Controllers
{
    [Route("api/message")]
    [ApiController]
    public class MessageController(
        IHubContext<ChatHub> hubContext,
        INotificationService notifService,
        MessageService msgService

    ) : Controller
    {
        private readonly IHubContext<ChatHub> _hubContext = hubContext;
        private readonly INotificationService _notifService = notifService;
        private readonly MessageService _msgService = msgService;

        [HttpPost("public")]
        [Authorize]
        public async Task<IActionResult> PublicReceiveMessageAndNotify(
            [FromBody] MessageRequest msgReq
        ) {

            try {
                List<NearbyUser> nearbyUsers = await _msgService.GetNearbyUsersAsync(msgReq.Longitude, msgReq.Latitude, User.GetId());

                List<NearbyUserWithConnId> nuwConnId = await _msgService.GetConnectionIdsAsync(nearbyUsers);

                MessageResponse msgRes = MessageFactory.CreateMessageResponse(msgReq, User.GetId(), true);

                //List<Task> tasks = this.CreateMsgTasksForParallel(msgRes, nuwConnId);
                 
                //await Task.WhenAll(tasks);

                return Ok(msgRes);
            }
            catch (Exception ex) {
                return StatusCode(500, "Failed to send message: " + ex.Message + ex.InnerException);
            }
        }

        [HttpPost("private")]
        [Authorize]
        public async Task<IActionResult> PrivateReceiveMessageAndNotify(
            [FromBody] PrivateMessageRequest msgReq
        ) {
            try
            {
                string recipientConnId = await _msgService.GetConnectionIdAsync( msgReq.MsgRecipientId );

                MessageResponse msgRes = MessageFactory.CreateMessageResponse(msgReq, User.GetId(), false, msgReq.MsgRecipientId);

                if (recipientConnId != null)
                {
                    await _hubContext.Clients.Client(recipientConnId).SendAsync("ReceiveMessage", msgRes);
                }
                else
                {
                    _notifService.SendPushNotification(msgReq.MsgRecipientId, msgRes);
                }

                return Ok( msgRes );
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
                            await _hubContext.Clients.Client(user.connId).SendAsync("ReceiveMessage", msgRes);
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