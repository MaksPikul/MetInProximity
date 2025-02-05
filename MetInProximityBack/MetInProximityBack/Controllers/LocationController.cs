﻿using MetInProximityBack.Data;
using MetInProximityBack.Extensions;
using MetInProximityBack.Interfaces;
using MetInProximityBack.Models;
using MetInProximityBack.Types.Location;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;

namespace MetInProximityBack.Controllers
{
    [Route("api/location")]
    [ApiController]
    public class LocationController(
        UserManager<AppUser> userManager ,
        INoSqlDb cosmosDb

    ) : Controller 
    {

        private readonly UserManager<AppUser> _userManager = userManager;
        private readonly INoSqlDb _cosmosDb = cosmosDb;


        [HttpPut]
        [Authorize]
        public async Task<IActionResult> PutUserLocation(
            [FromQuery(Name = "long")] double longitude,
            [FromQuery(Name = "lat")] double latitude
        ) {

            try
            {
                AppUser user = await _userManager.FindByEmailAsync(User.GetEmail());

                LocationObject locObj = LocObjFactory.CreateLocObj(user.Id, longitude, latitude);

                _cosmosDb.AddLocation(locObj);

                return Ok("Succeeded to update User Location");
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Failed to update User Location");
            }
        }

        



    }
}
