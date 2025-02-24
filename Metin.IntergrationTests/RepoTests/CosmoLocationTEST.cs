using MetInProximityBack.Repositories;
using MetInProximityBack.Types.Location;
using Microsoft.Azure.Cosmos;
using Microsoft.Azure.Cosmos.Spatial;
using static System.Runtime.InteropServices.JavaScript.JSType;

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
            var locObj_0 = new LocationObject
            {
                UserId = "1234",
                Location = new Point(0.0, 0.0)
            };

            ItemResponse<LocationObject> response_0 = await _cosmoRepo.AddOrUpdateLocation( locObj_0 );

            // Item added
            Assert.Equal("200", response_0.StatusCode.ToString());

            LocationObject? locObj_Response = await _cosmoRepo.GetLocationByUserId(locObj_0.UserId);

            // Item can be fetched
            Assert.Equal(locObj_0, locObj_Response);

            var locObj_1 = new LocationObject
            {
                UserId = "1234",
                Location = new Point(20.0, 20.0)
            };

            ItemResponse<LocationObject> response_1 = await _cosmoRepo.AddOrUpdateLocation(locObj_0);

            // Item replaced
            Assert.Equal("201", response_1.StatusCode.ToString());

            await _cosmoRepo.RemoveLocation( locObj_1 );

            LocationObject? locObj = await _cosmoRepo.GetLocationByUserId( locObj_1.UserId );

            // Removed Should not be in db to be fetched
            Assert.Null( locObj );
        }

        [Fact]
        public async Task TEST_GetNearbyLocations()
        {

            // These Two locations are Close to each other, 
            // userId 1111 is fetching, 
            // only userId 2222 should be in nearby user list

            var locObj_0 = new LocationObject
            {
                UserId = "1111",
                Location = new Point(0.0, 0.0)
            };

            var locObj_1 = new LocationObject
            {
                UserId = "2222",
                Location = new Point(0.0, 0.0)
            };

            // This location is out of range

            var locObj_2 = new LocationObject
            {
                UserId = "3333",
                Location = new Point(/* TODO */)
            };

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
