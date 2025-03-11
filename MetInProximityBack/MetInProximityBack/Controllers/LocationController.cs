using MetInProximityBack.Enums;
using MetInProximityBack.Extensions;
using MetInProximityBack.Interfaces;
using MetInProximityBack.Models;
using MetInProximityBack.Repositories;
using MetInProximityBack.Services;
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
        CosmoLocationRepo cosmosDb,
        MapService mapService

    ) : Controller
    {

        private readonly CosmoLocationRepo _cosmosDb = cosmosDb;
        private readonly MapService _mapService = mapService;

        [HttpPut]
        [Authorize]
        public async Task<IActionResult> PutUserLocation(
            [FromBody] LonLatObject llObj
        ) {
            try
            {
                LocationObject locObj = LocationFactory
                    .CreateLocObj( User.GetId(), llObj.lon, llObj.lat,  true);

                await _cosmosDb.AddOrUpdateLocation(locObj);

                string mapImageBase64 = await _mapService.GetMapTiles( llObj.lon, llObj.lat );

                var response = new LocResObj {
                    lon = llObj.lon,
                    lat = llObj.lat,
                    mapImage = mapImageBase64
                };

                return Ok(response);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Failed to update User Location: {ex.Message} - {ex.InnerException}");
                return StatusCode(500, $"Failed to update User Location: {ex.Message} - {ex.InnerException}");
            }
        }

        [HttpGet("map")]
        [Authorize]
        public async Task<IActionResult> GetLocationMap(
            [FromQuery] double lon,
            [FromQuery] double lat
        ){
            try
            {
                string mapImageBase64 = await _mapService.GetMapTiles(lon, lat);

                return Ok(mapImageBase64);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Failed to fetch Mapl: " + ex.Message);
            }
        }
    }
}
