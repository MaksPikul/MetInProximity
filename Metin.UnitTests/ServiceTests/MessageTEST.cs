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

        public MessageTEST() {

            _mockCosmoRepo = new Mock<CosmoLocationRepo>();
            _mockRedisRepo = new Mock<RedisCacheRepo>();

            _msgService = new MessageService(
                _mockCosmoRepo.Object,
                _mockRedisRepo.Object
            );
        }

        // GetNearbyUsersAsync
        // Tested with CosmoRepo

        // GetConnectionId
        // Tested with RedisRepo

        // GetLatestLocationAsync
        // Tested With CosmoRepo


        public async Task TEST_GetConnectionIdsAsync()
        {
            // Arrange
            var testList = new List<NearbyUser>();

            testList.Add(new NearbyUser() { UserId = "0"}); // Has a connection Id Saved in redis
            testList.Add(new NearbyUser() { UserId = "1" }); // Doesnt Have a connection Id Saved in redis

            var returnedConnIds = new List<string> { "1", null };

            _mockRedisRepo
                .Setup(cs => cs.GetManyFromCacheAsync(
                    testList
                    .Select(user => AppConstants.ConnIdCacheKey(user.UserId))
                    .ToList())
                )
                .ReturnsAsync(returnedConnIds);

            // Act
            List<NearbyUserWithConnId> resultList = await _msgService.GetConnectionIdsAsync(testList);

            // Assert
            // Should hold user Ids and nullable connectionId
            Assert.Equal("0", resultList[0].UserId);
            Assert.Equal("1", resultList[1].UserId);

            Assert.NotNull(resultList[0]);
            Assert.Null(resultList[1]);
        }

        [Fact]
        public async Task TEST_UpdateLocation(){

            // Arrange
            var locObj = LocationFactory.CreateLocObj("1", 0.0, 0.0, false);

            // Act
            var changedLocation = await _msgService.UpdateLocation(locObj, "Location", new Point(2.2, 2.2));
            var changedId = await _msgService.UpdateLocation(locObj, "UserId", "2");
            var changedOpen = await _msgService.UpdateLocation(locObj, "openToMessages", true);

            var exeption = _msgService.UpdateLocation(locObj, "field that dont exist", true);

            // Assert
            Assert.NotEqual(changedLocation.Location, locObj.Location);
            Assert.NotEqual(changedId.UserId, locObj.UserId);
            Assert.NotEqual(changedId.openToMessages, locObj.openToMessages);

            var exception = Assert.ThrowsAsync<Exception>(() =>
                _msgService.UpdateLocation(locObj, "Field dont exist :(", true)
            );
        }
    }
}
