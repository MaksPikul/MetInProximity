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
using System.Net;
using Moq.Protected;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Configuration;
using Microsoft.AspNetCore.Identity;
using MetInProximityBack.Models;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;

namespace Metin.UnitTests.ServiceTests
{
    public class NotificationTEST
    {

        private readonly Mock<HttpMessageHandler> mockHttpHandler;

        private readonly NotificationService _notifService;
        private Mock<IHubClients> mockClients;



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

            var config = new Mock<IConfiguration>();
            config.Setup(x => x["Firebase:SecretKey"]).Returns("key");
            config.Setup(x => x["Firebase:Url"]).Returns("https://test.com/");

            // https://www.code4it.dev/blog/testing-httpclientfactory-moq/
            this.mockHttpHandler = new Mock<HttpMessageHandler>(MockBehavior.Strict);

            this.mockHttpHandler
                .Protected()
                .Setup<Task<HttpResponseMessage>>(
                    "SendAsync",
                    ItExpr.IsAny<HttpRequestMessage>(),
                    ItExpr.IsAny<CancellationToken>()
                )
                .ReturnsAsync(new HttpResponseMessage())
                .Verifiable(); // Track calls for verification


            var httpClient = new HttpClient(this.mockHttpHandler.Object)
            {
                BaseAddress = new Uri("https://test.com/")
            };

            var mockHttpClientFactory = new Mock<IHttpClientFactory>();

            mockHttpClientFactory
                .Setup(x => x.CreateClient(It.IsAny<string>())) 
                .Returns(httpClient);

            // https://code-maze.com/aspnetcore-identity-testing-usermanager-rolemanager/
            var mockUserManager = new Mock<UserManager<AppUser>>(
            new Mock<IUserStore<AppUser>>().Object,
                null, null, null, null, null, null, null, null
            );
            
            mockUserManager.Setup(_ => _.FindByIdAsync(It.IsAny<string>()))
                .ReturnsAsync(new AppUser { /* FcmToken = "TEST_TOKEN" */ });

            var fbMock = new Mock<FirebaseService>(
                   mockHttpClientFactory.Object,
                   config.Object,
                   mockUserManager.Object
               );

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

            var openNearbyUser = new NearbyUser { openToMessages = true };
            var closedNearbyUser = new NearbyUser { openToMessages = false };

            // In notificationService ...
            testList.Add(new NearbyUserWithConnId(closedNearbyUser, null)); // false, false
            testList.Add(new NearbyUserWithConnId(openNearbyUser, null)); // true, false

            testList.Add(new NearbyUserWithConnId(closedNearbyUser, "1")); // false, true
            testList.Add(new NearbyUserWithConnId(openNearbyUser, "2")); // true, true

            // Act
            await _notifService.RunPublicTasksAsync(testData.MsgRes, testList);

            // Assert
            this.mockHttpHandler
                .Protected()
                .Setup<Task<HttpResponseMessage>>(
                    "SendAsync",
                    ItExpr.IsAny<HttpRequestMessage>(),
                    ItExpr.IsAny<CancellationToken>()
                )
                .ReturnsAsync(new HttpResponseMessage())
                .Verifiable(); // Track calls for verification

            mockClients.Verify(x => x.Client("1"), Times.Never);
            mockClients.Verify(x => x.Client("2"), Times.Once);
        }

        [Fact] // Copies PrivateMessage method in message controller Flow, same below
        public async Task TEST_CreatePrivateTaskAsync_CallsFirebase()
        {
            // Arrange
            NotificationTestData testData = new NotificationTestData(null);

            // Act
            await _notifService.CreatePrivateTaskAsync(testData.RecipientConnId, testData.MsgRes);

            // Assert

            mockHttpHandler
                .Protected()
                .Verify(
                    "SendAsync",
                    Times.Once(), // Ensure it's called exactly once
                    ItExpr.Is<HttpRequestMessage>(req =>
                        req.Method == HttpMethod.Post &&
                        req.RequestUri.ToString() == "https://test.com/"
                    ),
                    ItExpr.IsAny<CancellationToken>()
                );

            mockClients.Verify(x => x.Client(null), Times.Never);
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



