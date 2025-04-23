using Metin.IntergrationTests.SetupFiles;
using MetInProximityBack.Types.Location;
using Microsoft.AspNetCore.Http;
using Microsoft.Azure.Cosmos;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Net;
using System.Net.Http.Json;
using System.Text;
using System.Threading.Tasks;
using MetInProximityBack.Tests.SetupFiles;

namespace Metin.IntergrationTests.ControllerTests
{
    [Collection("MetinTests")]
    public class LocationTests(CustomWebAppFactory factory) : IAsyncLifetime
    {

        public Task InitializeAsync() => Task.CompletedTask;
        public async Task DisposeAsync() => await factory.ResetDatabaseAsync();

        [Fact]
        public async Task TEST_PutUserLocation_ShouldSucceed()
        {
            // Arrange
            var longitude = 20.0;
            var latitude = -43.2;
            var open = false;
            string url = this.CreateUrl(longitude, latitude, open);

            LonLatObject lonLatObj = new LonLatObject
            {
                lon = longitude,
                lat = latitude,
            };

            var jsonContent = JsonConvert.SerializeObject(lonLatObj);

            var content = new StringContent(jsonContent, Encoding.UTF8, "application/json");

            // Act
            var httpResponse = await factory.HttpClient.PutAsync(url, content);

            HttpStatusCode statusCode = httpResponse.StatusCode;
            string responseBody = await httpResponse.Content.ReadAsStringAsync();

            var returnedLocObj = JsonConvert.DeserializeObject<LocResObj>(responseBody);

            // Assert 
            Assert.Equal(HttpStatusCode.OK, statusCode);

            Assert.Equal(longitude, returnedLocObj?.lon);
            Assert.Equal(latitude, returnedLocObj?.lat);
        }

        [Fact]
        public async Task TEST_PutUserLocation_ShouldFail()
        {
            // Arrange 
            var longitude = 20.0;
            var latitude = -43.2;
            var open = false;

            string url_0 = this.CreateUrl(longitude, latitude, open, fake_long: "STRING_VALUE");
            
            string url_1 = this.CreateUrl(null, latitude, open);

            var obj = new 
            {
                lon = "Wrong val",
                lat = "Wrong val",
                mapImage = "Wrong val"
            };

            var jsonContent = JsonConvert.SerializeObject(obj);

            var content = new StringContent(jsonContent, Encoding.UTF8, "application/json");

            // Act
            var response_0 = await factory.HttpClient.PutAsync(url_0, null);
            var response_1 = await factory.HttpClient.PutAsync(url_1, content);

            var responseBody_0 = await response_0.Content.ReadAsStringAsync();
            var returnedLocObj_0 = JsonConvert.DeserializeObject<LocResObj>(responseBody_0);

            var responseBody_1 = await response_0.Content.ReadAsStringAsync();
            var returnedLocObj_1 = JsonConvert.DeserializeObject<LocResObj>(responseBody_1);

            // Assert : STRING IN DOUBLE FIELD
            Assert.Equal(HttpStatusCode.InternalServerError, response_0.StatusCode);
            Assert.Equal(HttpStatusCode.InternalServerError, response_1.StatusCode);

            Assert.Equal(longitude, returnedLocObj_0?.lat);
            Assert.Equal(latitude, returnedLocObj_0?.lat);

            // Assert : NULL IN NON-NULLABLE DOUBLE FIELD
            Assert.Equal(HttpStatusCode.InternalServerError, response_1.StatusCode);

            Assert.Null(returnedLocObj_1?.lon);
            Assert.Null(returnedLocObj_1?.lat);
        }

        private string CreateUrl(double? longitude, double latitude, bool open, string fake_long = null)
        {
            if (fake_long == null)
            {
                return $"/api/user/location?long={longitude}&lat={latitude}&open={open}";
            }
            else
            {
                return $"/api/user/location?long={fake_long}&lat={latitude}&open={open}";
            }
        }
    }
}
