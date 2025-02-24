using Microsoft.AspNetCore.Mvc.Testing;
using System.Net.Http.Headers;
using MetInProximityBack.Types.Location;
using System.Net;
using Newtonsoft.Json;


// https://learn.microsoft.com/en-us/aspnet/core/test/integration-tests?view=aspnetcore-9.0

namespace MetInProximityBack.Tests.ControllerTests
{ 

    // IN PRODUCTION, WILL NEED TO USE A CUSTOM TEST SERVER, 
    public class LocationTEST : IClassFixture<WebApplicationFactory<Program>>
    {

        private readonly HttpClient _client;
        private double longitude;
        private double latitude;
        private bool open;
        private string userId;

        public LocationTEST(WebApplicationFactory<Program> factory)
        {

            _client = factory.CreateClient();
            _client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", SetupFiles.Constants.DOG_TEST_JWT);
            userId = "DOG-id-123";

        }

        [Fact]
        public async Task TEST_PutUserLocation_ShouldSucceed()
        {
            // Arrange
            longitude = 20.0;
            latitude = -43.2;
            open = false;
            string url = this.CreateUrl(longitude, latitude, open);

            // Act
            var response = await _client.PutAsync(url, null);

            var responseBody = await response.Content.ReadAsStringAsync();
            var returnedLocObj = JsonConvert.DeserializeObject<LocationObject>(responseBody);

            // Assert 
            Assert.Equal(HttpStatusCode.OK, response.StatusCode);
            Assert.Equal(userId, returnedLocObj?.UserId);
            Assert.Equal(longitude, returnedLocObj?.Location.Position.Longitude);
            Assert.Equal(latitude, returnedLocObj?.Location.Position.Latitude);
        }

        [Fact]
        public async Task TEST_PutUserLocation_ShouldFail()
        {
            // Arrange 
            longitude = 20.0;
            latitude = -43.2;
            open = false;

            string url_0 = this.CreateUrl(longitude, latitude, open, fake_long : "STRING_VALUE");
            string url_1 = this.CreateUrl(null, latitude, open);

            // Act
            var response_0 = await _client.PutAsync(url_0, null);
            var response_1 = await _client.PutAsync(url_1, null);

            var responseBody_0 = await response_0.Content.ReadAsStringAsync();
            var returnedLocObj_0 = JsonConvert.DeserializeObject<LocationObject>(responseBody_0);

            var responseBody_1 = await response_0.Content.ReadAsStringAsync();
            var returnedLocObj_1 = JsonConvert.DeserializeObject<LocationObject>(responseBody_1);

            // Assert : STRING IN DOUBLE FIELD
            Assert.Equal(HttpStatusCode.InternalServerError, response_0.StatusCode);

            Assert.Equal(userId, returnedLocObj_0?.UserId);
            Assert.Equal(longitude, returnedLocObj_0?.Location.Position.Longitude);
            Assert.Equal(latitude, returnedLocObj_0?.Location.Position.Latitude);

            // Assert : NULL IN NON-NULLABLE DOUBLE FIELD
            Assert.Equal(HttpStatusCode.InternalServerError, response_1.StatusCode);

            Assert.Null(returnedLocObj_1?.UserId);
            Assert.Null(returnedLocObj_1?.Location.Position.Longitude);
            Assert.Null(returnedLocObj_1?.Location.Position.Latitude);
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
