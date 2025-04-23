using MetInProximityBack.Services.Notifications;
using MetInProximityBack.Services;
using Moq;
using MetInProximityBack.Factories;
using MetInProximityBack.Types.Message;
using MetInProximityBack.Hubs;
using Microsoft.AspNetCore.SignalR;
using MetInProximityBack.Types.NearbyUser;
using MetInProximityBack.Interfaces.IServices;
using Microsoft.EntityFrameworkCore;
using MetInProximityBack.Data;
using MetInProximityBack.Interfaces.IRepos;

namespace Metin.UnitTests.ServiceTests
{
    public class NotificationTEST
    {

        private readonly Mock<HttpMessageHandler> mockHttpHandler;

        private readonly INotificationService _notifService;
        private Mock<IHubClients> mockClients;
        private Mock<IPushNotifService> fbMock;

        public NotificationTEST()
        {
            // From this i now know that Moq doesnt like extensions
            // https://stackoverflow.com/questions/56254258/mock-signalr-hub-for-testing-dependent-class
            // https://www.codeproject.com/Articles/1266538/Testing-SignalR-Hubs-in-ASP-NET-Core-2-1
            
            // ----------- SIGNAL R -----------------

            var mockClientProxy = new Mock<ISingleClientProxy>();
            this.mockClients = new Mock<IHubClients>();
            var mockHubContext = new Mock<IHubContext<ChatHub>>();

            this.mockClients.Setup(x => x.Client(It.IsAny<string>())).Returns(mockClientProxy.Object);
            mockHubContext.Setup(x => x.Clients).Returns(this.mockClients.Object);

            var signalRService = new SignalRService(mockHubContext.Object);

            // -----------------------------------------

            // ---------- FireBase ------------------

            var options = new DbContextOptionsBuilder<AppDbContext>()
                .UseInMemoryDatabase(databaseName: "TestDb")
                .Options;

            var context = new AppDbContext(options);

            fbMock = new Mock<IPushNotifService>(
            );
            fbMock.Setup(x => x.SendPushNotification(It.IsAny<string>(), It.IsAny<MessageResponse>())).Returns(Task.CompletedTask);

            // make fb verify calls

            _notifService = new NotificationService(
               new SignalRService(mockHubContext.Object),
               fbMock.Object
            );
        }

        [Fact]
        public async Task TEST_CreatePublicTasksAsync()
        {
            // Arrange
            NotificationTestData testData = new NotificationTestData(null);
           
            var testList = new List<NearbyUserWithConnId>();

            var openNearbyUser = new NearbyUser { UserId = "user1", openToMessages = true };
            var closedNearbyUser = new NearbyUser { UserId = "user2", openToMessages = false };

            // In notificationService ...
            // Connection Id not found
            testList.Add(new NearbyUserWithConnId(closedNearbyUser, null)); // false, false
            testList.Add(new NearbyUserWithConnId(openNearbyUser, null)); // true, false

            // Connection Id found
            testList.Add(new NearbyUserWithConnId(closedNearbyUser, "1")); // false, true
            testList.Add(new NearbyUserWithConnId(openNearbyUser, "2")); // true, true

            // Act
            await _notifService.RunPublicTasksAsync(testData.MsgRes, testList);

            // Assert
            // SignalR checks
            mockClients.Verify(x => x.Client("1"), Times.Never);
            mockClients.Verify(x => x.Client("2"), Times.Once);

            // Firebase checks
            fbMock.Verify(x => x.SendPushNotification("user1", testData.MsgRes), Times.Once);
            fbMock.Verify(x => x.SendPushNotification("user2", testData.MsgRes), Times.Never);
        }

        [Fact] // Copies PrivateMessage method in message controller Flow, same below
        public async Task TEST_CreatePrivateTaskAsync_CallsFirebase()
        {
            // Arrange
            NotificationTestData testData = new NotificationTestData(null);

            // Act
            await _notifService.CreatePrivateTaskAsync(testData.RecipientConnId, testData.MsgRes);

            // Assert
            fbMock.Verify(x => x.SendPushNotification(testData.RecipientConnId, testData.MsgRes), Times.Once);
        }

        [Fact]
        public async Task TEST_CreatePrivateTaskAsync_CallsSignalR()
        {
            // Arrange
            NotificationTestData testData = new NotificationTestData("456");

            // Act
            await _notifService.CreatePrivateTaskAsync(testData.RecipientConnId, testData.MsgRes);

            // Assert
            mockClients.Verify(x => x.Client("456"), Times.Once);
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
                    lat = 20.0,
                    lon = 20.0,
                    Body = "TEST MESSAGE BODY"
                };

                MsgRes = MessageFactory.CreateMessageResponse(MsgReq, "DOG_id_123", false, MsgReq.MsgRecipientId);
                RecipientConnId = connId;
            }
        }
    }
}



