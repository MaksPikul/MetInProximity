using MetInProximityBack.Services.Notifications;
using MetInProximityBack.Services;
using Moq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using MetInProximityBack.Repositories;

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


        public MessageTEST() { }



        public async Task TEST_GetConnectionIdsAsync()
        {

        }

        [Fact]
        public async Task TEST_UpdateLocation(){
        }
    }
}
