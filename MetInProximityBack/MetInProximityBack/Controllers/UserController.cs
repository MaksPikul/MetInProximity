﻿using MetInProximityBack.Data;
using MetInProximityBack.Extensions;
using MetInProximityBack.Interfaces.IServices;
using MetInProximityBack.Services;
using MetInProximityBack.Services.Tokens;
using MetInProximityBack.Types.Location;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using StackExchange.Redis;

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

        [HttpGet("private_users")]
        [Authorize]
        public async Task<IActionResult> GetAvailableForPrivateUserMsg(
            [FromBody] LonLatObject locObj
        ) {
            List<NearbyUser> nearbyUsers = await _msgService.GetNearbyUsersAsync( locObj.lon, locObj.lat, User.GetId() );

            IEnumerable<NearbyUser> usersOpenToPrivate = nearbyUsers.Where(x => x.openToPrivate == true);

            return Ok(usersOpenToPrivate);
        }

        [HttpPatch("visibility")]
        [Authorize]
        public async Task<IActionResult> UpdateUserAvailableForPrivateMsg()
        {
            string openToPrivate = User.GetOpenToPrivate();

            if (openToPrivate != "true" || openToPrivate != "false")
            {
                return BadRequest("FIELD ERROR");
            }

            bool openToPrivateBool = true;
            if (openToPrivate == "true")
            {
                openToPrivateBool = false;
            }

            LocationObject? locObj = await _msgService.GetLatestLocationAsync( User.GetId() );

            await _msgService.UpdateLocation(locObj, "openToPrivate", openToPrivate);

            string newAccessToken = _authTokenService.CreateAccessToken(User, openToPrivateBool); 

            return Ok(newAccessToken);
        }

        [HttpPatch("update")]
        [Authorize]
        public async Task<IActionResult> UpdateUserFcmToken(
            [FromQuery(Name = "token")] string token
        )
        {
            Console.WriteLine("before");
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
