using MetInProximityBack.Constants;
using MetInProximityBack.Types.Location;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Azure.Cosmos;
using System.Collections;

namespace MetInProximityBack.Controllers
{
    public class TestController : Controller
    {
        private Container _container;
        public TestController(CosmosClient cosmosClient)
        {
            _container = cosmosClient.GetContainer(
                AppConstants.COSMO_LOC_DB,
                AppConstants.COSMO_LOC_CON
            );
        }

        [HttpPost("test")]
        public async Task<IActionResult> Index(
            [FromBody] LonLatObject obj)
        {
            try
            {
                ArrayList CosmoDbDummyData = new ArrayList
                {
                    // Nearby and available for private
                    LocationFactory.CreateLocObj("1_near&priv",obj.lon+0.00002889, obj.lat+(-0.00060548), true, true),
                    LocationFactory.CreateLocObj("2_near&priv", obj.lon+0.00216696, obj.lat+0.00000003, true, true),
                    LocationFactory.CreateLocObj("3_near&priv", obj.lon+-0.00138628, obj.lat+0.00225841, true, true), 

                    // Not available to private but nearby
                    LocationFactory.CreateLocObj("4_near", obj.lon+(-0.00040432), obj.lat+0.00050540, true, false),
                    LocationFactory.CreateLocObj("5_near", obj.lon+0.00028890, obj.lat+0.00060548, true, false),

                    // Not nearby
                    LocationFactory.CreateLocObj("6", obj.lon, obj.lat+0.0111, true, true),
                    LocationFactory.CreateLocObj("7", obj.lon+0.0111, obj.lat, true, true),
                    LocationFactory.CreateLocObj("8", obj.lon+0.0111, obj.lat+0.0111, true, true)
                };

                foreach (LocationObject dummyUser in CosmoDbDummyData)
                {
                    await _container.UpsertItemAsync(dummyUser, new PartitionKey(dummyUser.UserId));
                }

                Console.WriteLine("CosmoDb (NoSql) has been populated with new dummy data");

                return Ok(CosmoDbDummyData);
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Failed to add test data: " + ex.ToString());
            }
        }
    }
}
