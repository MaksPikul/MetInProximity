using MetInProximityBack.Services.Notifications;
using MetInProximityBack.Services;
using Moq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using MetInProximityBack.Repositories;
using MetInProximityBack.Types.Location;
using System.Threading.Channels;
using Microsoft.Azure.Cosmos.Spatial;
using MetInProximityBack.Constants;
using Microsoft.Azure.Cosmos;
using StackExchange.Redis;
using Xunit.Abstractions;

namespace Metin.UnitTests.ServiceTests
{
    // Dont need to test all functions in this class, 
    // Because most are just wrappers for Cosmo/Redis Repos

    // These two have some sort of logic which other than send/get data


   



    public class MessageTEST
    {
        private readonly Mock<CosmoLocationRepo> _mockCosmoRepo;
        private readonly Mock<RedisCacheRepo> _mockRedisRepo;
        private readonly MessageService _msgService;



        public interface IDatabase_Mock
        {
            Task<List<string>> StringGetAsync(RedisKey[] keys);
        }

        private Mock<RedisCacheRepo> MockRedis()
        {
            var mockDB = new Mock<IDatabase>();

            // https://stackoverflow.com/questions/79040132/cant-setup-mocked-redis-idatabase-using-moq
            mockDB
                .Setup(_ => _.StringGetAsync(It.IsAny<RedisKey[]>(), It.IsAny<CommandFlags>()))
                .ReturnsAsync(new RedisValue[] { "connId1", RedisValue.Null });

            var _mockRedisRepo = new Mock<RedisCacheRepo>(mockDB.Object);

            return _mockRedisRepo;

        }

        // https://stackoverflow.com/questions/59067239/i-need-xunit-test-case-of-this-microsoft-azure-cosmos-container
        private Mock<CosmoLocationRepo> MockCosmos()
        {
            var container = new Mock<Container>();
            var client = new Mock<CosmosClient>();

            client.Setup(x => x.GetContainer(It.IsAny<string>(), It.IsAny<string>())).Returns(container.Object);


            var mockResponse = new Mock<ItemResponse<LocationObject>>();
            mockResponse.SetupGet(r => r.Resource).Returns(new LocationObject
            {
                UserId = "1234",
                Location = new Point(20.0, 20.0)
            });

            container.Setup(c => c.UpsertItemAsync(
                It.IsAny<LocationObject>(),
                It.IsAny<PartitionKey>(),
                It.IsAny<ItemRequestOptions>(),
                It.IsAny<CancellationToken>())
            ).ReturnsAsync(mockResponse.Object);

            var _mockCosmoRepo = new Mock<CosmoLocationRepo>(client.Object);

            return _mockCosmoRepo;
        }



        public MessageTEST(ITestOutputHelper output) {

            _msgService = new MessageService(
                MockCosmos().Object,
                MockRedis().Object
            );
        }

        [Fact]
        public async Task TEST_GetConnectionIdsAsync()
        {
            // Arrange
            var testList = new List<NearbyUser>();

            testList.Add(new NearbyUser() { UserId = "0"}); // Has a connection Id Saved in redis
            testList.Add(new NearbyUser() { UserId = "1" }); // Doesnt Have a connection Id Saved in redis

            // Act
            List<NearbyUserWithConnId> resultList = await _msgService.GetConnectionIdsAsync(testList);

            // Assert
            // Should hold user Ids and nullable connectionId
            Assert.Equal("0", resultList[0].UserId);
            Assert.Equal("1", resultList[1].UserId);

            Assert.NotNull(resultList[0].connId);
            Assert.Null(resultList[1].connId);
        }

        [Fact]
        public async Task TEST_UpdateLocation(){

            // Arrange
            var locObj = LocationFactory.CreateLocObj("1", 0.0, 0.0, false);

            // Act locObj changes inside the method
            await _msgService.UpdateLocation(locObj, "Location", new Point(2.2, 2.2));
            await _msgService.UpdateLocation(locObj, "UserId", "2");
            await _msgService.UpdateLocation(locObj, "openToMessages", true);

            var exeption = _msgService.UpdateLocation(locObj, "field that dont exist", true);

            // Assert
            Assert.NotEqual(0.0, locObj.Location.Position.Latitude);
            Assert.NotEqual("1", locObj.UserId);
            Assert.NotEqual(false, locObj.openToMessages);

            var exception = Assert.ThrowsAsync<Exception>(() =>
                _msgService.UpdateLocation(locObj, "Field dont exist :(", true)
            );
        }
    }
}
