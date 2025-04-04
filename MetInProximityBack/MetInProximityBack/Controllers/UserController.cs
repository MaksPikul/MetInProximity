using MetInProximityBack.Data;
using MetInProximityBack.Enums;
using MetInProximityBack.Extensions;
using MetInProximityBack.Interfaces.IServices;
using MetInProximityBack.Services;
using MetInProximityBack.Services.Tokens;
using MetInProximityBack.Types.Location;
using MetInProximityBack.Types.NearbyUser;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using StackExchange.Redis;
using static System.Runtime.InteropServices.JavaScript.JSType;

namespace MetInProximityBack.Controllers
{
    [Route("api/user")]
    [ApiController]
    public class UserController(
        IMessageService msgService,
        AuthTokenService authTokenService,
        AppDbContext appDbContext
    ) : Controller
    {
        private readonly IMessageService _msgService = msgService;
        private readonly AuthTokenService _authTokenService = authTokenService;
        private readonly AppDbContext _appDbContext = appDbContext;

        [Authorize]
        [HttpGet("private")]
        public async Task<IActionResult> GetNearbyPrivateUsers(
            [FromQuery] double lon,
            [FromQuery] double lat
        ) {
            try
            {
                    
                List<NearbyUser> nearbyUsers = await _msgService.GetNearbyUsersAsync(lon, lat, User.GetId());

                var usersOpenToPrivate = nearbyUsers.Where(x => x.openToPrivate == true).Select(nu => new { Id = nu.UserId }).ToList();

                return Ok(usersOpenToPrivate);
                   
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }

        }

        [HttpPatch("visibility")]
        [Authorize]
        public async Task<IActionResult> UpdateUserVisibility()
        {
            try
            {
                string openToPrivate = User.GetOpenToPrivate();

                if (openToPrivate != "True" && openToPrivate != "False")
                {
                    return BadRequest("FIELD ERROR, openToPrivate does not correspond to 'TRUE' or 'FALSE'");
                }

                bool openToPrivateBool = true;
                if (openToPrivate == "True")
                {
                    openToPrivateBool = false;
                }

                LocationObject? locObj = await _msgService.GetLatestLocationAsync( User.GetId() );

                if (locObj != null)
                {
                    await _msgService.UpdateLocation(locObj, "openToPrivate", openToPrivateBool);
                    Console.WriteLine("updates location");
                }

                string newAccessToken = _authTokenService.CreateAccessToken(User, openToPrivateBool);
                Console.WriteLine("Creates Access Token");

                return Ok(new { message = newAccessToken });
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        [HttpPatch("update")]
        [Authorize]
        public async Task<IActionResult> UpdateUserFcmToken(
            [FromQuery(Name = "token")] string token
        )
        {
            await _appDbContext
                .Users
                .Where(u => u.Id == User.GetId())
                .ExecuteUpdateAsync(s =>
                    s.SetProperty(u => u.FcmToken, token)
                );
            
            return Ok();
        }


    }

}
