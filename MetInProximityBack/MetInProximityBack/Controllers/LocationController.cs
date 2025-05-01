using MetInProximityBack.Interfaces.IServices;
using MetInProximityBack.Extensions;
using MetInProximityBack.Interfaces.IRepos;
using MetInProximityBack.Types.Location;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace MetInProximityBack.Controllers
{
    [Route("api/location")]
    [ApiController]
    public class LocationController(
        IDocumentRepo cosmosDb,
        IMapService mapService
    ) : Controller
    {
        private readonly IDocumentRepo _cosmosDb = cosmosDb;
        private readonly IMapService _mapService = mapService;

        [HttpPut]
        [Authorize]
        public async Task<IActionResult> PutUserLocation(
            [FromBody] LonLatObject llObj
        ) {
            try
            {

                Console.WriteLine("Coords received: lon = " + llObj.lon + ", lat = " + llObj.lat);

                LocationObject locObj = LocationFactory
                    .CreateLocObj( User.GetId(), llObj.lon, llObj.lat,  true);

                await _cosmosDb.AddOrUpdateLocation(locObj);

                string mapImageBase64 = await _mapService.GetMapTiles( llObj.lon, llObj.lat );

                var response = LocationFactory
                    .CreateLocResObj( llObj.lon, llObj.lat, mapImageBase64 );

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

                return Ok(new {message=mapImageBase64});
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Failed to fetch Mapl: " + ex.Message);
            }
        }
    }
}
