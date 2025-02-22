using MetInProximityBack.Enums;
using MetInProximityBack.Extensions;
using MetInProximityBack.Interfaces;
using MetInProximityBack.Models;
using MetInProximityBack.Repositories;
using MetInProximityBack.Types.Location;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Azure.Cosmos;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;

namespace MetInProximityBack.Controllers
{
    [Route("api/location")]
    [ApiController]
    public class LocationController(
        UserManager<AppUser> userManager,
        CosmoLocationRepo cosmosDb

    ) : Controller
    {
        
        private readonly UserManager<AppUser> _userManager = userManager;
        private readonly CosmoLocationRepo _cosmosDb = cosmosDb;

        // TEST MANUALLY, WORKS
        [HttpPut]
        [Authorize]
        public async Task<IActionResult> PutUserLocation(
            [FromQuery(Name = "long")] double longitude,
            [FromQuery(Name = "lat")] double latitude,
            [FromQuery(Name = "open")] bool open
        ) {

            try
            {
                var claims = User.Claims.ToList();

                LocationObject locObj = LocationFactory
                    .CreateLocObj( User.GetId(), longitude, latitude, open);

                await _cosmosDb.AddOrUpdateLocation(locObj);

                return Ok(locObj);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Failed to update User Location: {ex.Message} - {ex.InnerException}");
                return StatusCode(500, $"Failed to update User Location: {ex.Message} - {ex.InnerException}");
            }
        }


    }
}
