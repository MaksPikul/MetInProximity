using MetInProximityBack.Enums;
using MetInProximityBack.Models;
using MetInProximityBack.Types.Location;
using MetInProximityBack.Types.Message;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using MetInProximityBack.Factories;
using MetInProximityBack.Extensions;
using Microsoft.AspNetCore.Authorization;
using MetInProximityBack.Interfaces.IServices;
using Microsoft.AspNetCore.RateLimiting;

namespace MetInProximityBack.Controllers
{
    [Route("api/message")]
    [ApiController]

    public class MessageController(
        IMessageService msgService,
        INotificationService notificationService

    ) : Controller
    {
        private readonly IMessageService _msgService = msgService;
        private readonly INotificationService _notifService = notificationService;

        [HttpPost("public")]
        [Authorize]
        [EnableRateLimiting("chat")]
        public async Task<IActionResult> PublicReceiveMessageAndNotify(
            [FromBody] MessageRequest msgReq
        ) {
            try {
                List<NearbyUser> nearbyUsers = await _msgService.GetNearbyUsersAsync(msgReq.lon, msgReq.lat, User.GetId());

                List<NearbyUserWithConnId> nearUserswConnId = await _msgService.GetConnectionIdsAsync(nearbyUsers);

                MessageResponse msgRes = MessageFactory.CreateMessageResponse(msgReq, User.GetId(), true);

                await _notifService.RunPublicTasksAsync(msgRes, nearUserswConnId);

                return Ok(msgRes);
            }
            catch (Exception ex) {
                return StatusCode(500, "Failed to send message: " + ex.Message + ex.InnerException);
            }
        }

        [HttpPost("private")]
        [Authorize]
        [EnableRateLimiting("chat")]
        public async Task<IActionResult> PrivateReceiveMessageAndNotify(
            [FromBody] PrivateMessageRequest msgReq
        ) {
            try
            {
                string recipientConnId = await _msgService.GetConnectionIdAsync( msgReq.MsgRecipientId );

                MessageResponse msgRes = MessageFactory.CreateMessageResponse(msgReq, User.GetId(), false, msgReq.MsgRecipientId);

                await _notifService.CreatePrivateTaskAsync(recipientConnId, msgRes);

                return Ok( msgRes );
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Failed to send message: " + ex.Message);
            }
        }
    }
}