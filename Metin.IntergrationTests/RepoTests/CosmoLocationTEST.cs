using MetInProximityBack.Repositories;
using MetInProximityBack.Types.Location;
using MetInProximityBack.Types.NearbyUser;
using Microsoft.Azure.Cosmos;
using Microsoft.Azure.Cosmos.Spatial;

namespace MetInProximityBack.Tests.ServiceTests
{
    public class CosmoLocationTEST
    {

        private readonly CosmoLocationRepo _cosmoRepo;

        public CosmoLocationTEST() {

            CosmosClientOptions options = new()
            {
                HttpClientFactory = () => new HttpClient(new HttpClientHandler()
                {
                    ServerCertificateCustomValidationCallback = HttpClientHandler.DangerousAcceptAnyServerCertificateValidator
                }),
                ConnectionMode = ConnectionMode.Gateway,
            };

            // below is a secret,
            // which is not a secret, because its whats given on Microsoft docs to use the emulator
            // ^ FYI 
            var client =new CosmosClient(
                accountEndpoint : "https://localhost:8081",
                authKeyOrResourceToken : "C2y6yDjf5/R+ob0N8A7Cgv30VRDJIWEHLM+4QDU5DE2nQ9nDuVTqobD4b8mGGyPMbIZnqyMsEcaGQy67XIw/Jw==",
                clientOptions: options
            );

            _cosmoRepo = new CosmoLocationRepo(
                client
            );
        }


        [Fact] // Some long function name lol
        public async Task TEST_AddOrUpdateLocation_GetLocationByUserId_RemoveLocation()
        {

            var locObj_0 = LocationFactory.CreateLocObj("1234", 0.0, 0.0, false);

            ItemResponse<LocationObject> response_0 = await _cosmoRepo.AddOrUpdateLocation( locObj_0 );

            // Item added
            Assert.Equal("Created", response_0.StatusCode.ToString());

            LocationObject? locObj_Response = await _cosmoRepo.GetLocationByUserId(locObj_0.UserId);

            // Item can be fetched
            Assert.Equal(locObj_0, locObj_Response);

            var locObj_1 = LocationFactory.CreateLocObj("1234", 20.0, 0.0, false);

            ItemResponse<LocationObject> response_1 = await _cosmoRepo.AddOrUpdateLocation(locObj_0);

            // Item replaced
            Assert.Equal("OK", response_1.StatusCode.ToString());

            await _cosmoRepo.RemoveLocation( locObj_1 );

            LocationObject? locObj = await _cosmoRepo.GetLocationByUserId( "1234" );

            // Removed Should not be in db to be fetched
            Assert.Null( locObj );
        }

        [Fact]
        public async Task TEST_GetLocationByUserId_returnsLatestEntry()
        {
            // Arrange 
            var locObj_0 = new LocationObject
            {
                UserId = "1234",
                Location = new Point(0.0, 0.0)
            };
            var locObj_1 = new LocationObject
            {
                UserId = "1234",
                Location = new Point(20.0, 0.0)
            };

            // Act
            ItemResponse<LocationObject> response_0 = await _cosmoRepo.AddOrUpdateLocation(locObj_0);
            ItemResponse<LocationObject> response_1 = await _cosmoRepo.AddOrUpdateLocation(locObj_1);

            LocationObject? latestLocObj = await _cosmoRepo.GetLocationByUserId("1234");

            // Assert
            Assert.Equal(latestLocObj, locObj_1);

            //DB clean up
            await _cosmoRepo.RemoveLocation(locObj_1);
        }

        [Fact]
        public async Task TEST_GetNearbyLocations()
        {

            // These Two locations are Close to each other, 
            // userId 1111 is fetching, 
            // only userId 2222 should be in nearby user list

            // Person At Qmul Clock Tower
            var locObj_0 = LocationFactory.CreateLocObj("1111", -0.040175, 51.522996, false);

            // Person in range to user 1111
            // Person at Qmul Library
            var locObj_1 = LocationFactory.CreateLocObj("2222", -0.039617, 51.524331, false);

            // This location is out of range
            // Person at Mile end Church
            var locObj_2 = LocationFactory.CreateLocObj("3333", -0.035602, 51.524718, false);

            ItemResponse<LocationObject> response_0 = await _cosmoRepo.AddOrUpdateLocation(locObj_0);
            ItemResponse<LocationObject> response_1 = await _cosmoRepo.AddOrUpdateLocation(locObj_1);
            ItemResponse<LocationObject> response_2 = await _cosmoRepo.AddOrUpdateLocation(locObj_2);

            List<NearbyUser> nearbyUsers = await _cosmoRepo.GetNearbyLocations(
                locObj_0.Location,
            locObj_0.UserId
            );

            // User Which is in range and not requesting user
            Assert.True(nearbyUsers.Any(d => d.UserId == "2222"));

            // Users which are 1. the requesting user, 2. not in range
            Assert.False(nearbyUsers.Any(d => d.UserId == "1111"));
            Assert.False(nearbyUsers.Any(d => d.UserId == "3333"));

            Assert.True(nearbyUsers.Count == 1);

            // Clean Up DB
            await _cosmoRepo.RemoveLocation(locObj_0);
            await _cosmoRepo.RemoveLocation(locObj_1);
            await _cosmoRepo.RemoveLocation(locObj_2);
        }
    }
}
