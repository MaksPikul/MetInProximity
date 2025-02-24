using MetInProximityBack.Services.Notifications;
using MetInProximityBack.Services;
using Moq;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using MetInProximityBack.Factories;
using Microsoft.Azure.Cosmos;
using MetInProximityBack.Types.Message;
using MetInProximityBack.Types.Location;
using Microsoft.Azure.Cosmos.Serialization.HybridRow;
using MetInProximityBack.Hubs;
using Microsoft.AspNetCore.SignalR;
using System.Dynamic;
using Microsoft.Extensions.Caching.StackExchangeRedis;
using MetInProximityBack.Repositories;
using Microsoft.EntityFrameworkCore.Storage;
using Microsoft.Azure.Cosmos.Linq;
using Castle.Core.Configuration;
using System.Net;
using Moq.Protected;

namespace Metin.UnitTests.ServiceTests
{
    public class NotificationTEST
    {

        private readonly Mock<SignalRService> _mockSrService;
        private readonly Mock<FirebaseService> _mockFbService;
        private readonly NotificationService _notifService;


        private Mock<SignalRService> MockSignalR()
        {

        }

        private Mock<FirebaseService> MockFirebase()
        {
            
        }


        public NotificationTEST()
        {
            _mockSrService = new Mock<SignalRService>();
            _mockFbService = new Mock<FirebaseService>();

            _notifService = new NotificationService(
                _mockSrService.Object,
                _mockFbService.Object
            );
        }

        [Fact]
        public async Task TEST_CreatePublicTasksAsync()
        {
            // Arrange
            NotificationTestData testData = new NotificationTestData(null);
           
            var testList = new List<NearbyUserWithConnId>();

            var openNearbyUser = new NearbyUser { openToMessages = true };
            var closedNearbyUser = new NearbyUser { openToMessages = false };

            // In notificationService ...
            testList.Add(new NearbyUserWithConnId(closedNearbyUser, null)); // false, false
            testList.Add(new NearbyUserWithConnId(openNearbyUser, null)); // true, false

            testList.Add(new NearbyUserWithConnId(closedNearbyUser, "1")); // false, true
            testList.Add(new NearbyUserWithConnId(openNearbyUser, "2")); // true, true

            // Act
            _notifService.CreatePublicTasksAsync(testData.MsgRes, testList);

            // Assert
            _mockFbService.Verify(f => f.SendPushNotification(null , testData.MsgRes), Times.Once);

            _mockSrService.Verify(f => f.SendNotification("1",testData.MsgRes), Times.Never);
            _mockSrService.Verify(f => f.SendNotification("2", testData.MsgRes), Times.Once);
        }

        [Fact] // Copies PrivateMessage method in message controller Flow, same below
        public async Task TEST_CreatePrivateTaskAsync_CallsFirebase()
        {
            // Arrange
            NotificationTestData testData = new NotificationTestData(null);

            // Act
            await _notifService.CreatePrivateTaskAsync(testData.RecipientConnId, testData.MsgRes);

            // Assert
            _mockFbService.Verify(f => f.SendPushNotification(testData.RecipientConnId, testData. MsgRes), Times.Once);
        }

        [Fact]
        public async Task TEST_CreatePrivateTaskAsync_CallsSignalR()
        {
            // Arrange
            NotificationTestData testData = new NotificationTestData("456");

            // Act
            await _notifService.CreatePrivateTaskAsync(testData.RecipientConnId, testData.MsgRes);

            // Assert
            _mockSrService.Verify(f => f.SendNotification(testData.RecipientConnId, testData.MsgRes), Times.Once);
        }
    
        internal class NotificationTestData
        {
            public PrivateMessageRequest MsgReq { get; set; }
            public MessageResponse MsgRes { get; set; }
            public string? RecipientConnId { get; set; }

            public NotificationTestData(string? connId)
            {
                MsgReq = new PrivateMessageRequest
                {
                    MsgRecipientId = "CAT-id-456",
                    Latitude = 20.0,
                    Longitude = 20.0,
                    Body = "TEST MESSAGE BODY"
                };

                MsgRes = MessageFactory.CreateMessageResponse(MsgReq, "DOG_id_123", false, MsgReq.MsgRecipientId);
                RecipientConnId = connId;
            }
        }
    }
}



