using Metin.IntergrationTests.SetupFiles;
using MetInProximityBack.Types.Message;
using Microsoft.AspNetCore.Connections;
using Microsoft.AspNetCore.SignalR.Client;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Policy;
using System.Text;
using System.Threading.Tasks;

namespace MetInProximityBack.Tests.ControllerTests
{
    //[Collection("MetinTests")]
    public class MessageTests : IAsyncLifetime
    {
        public Task InitializeAsync() => Task.CompletedTask;
        public async Task DisposeAsync() => await _factory.ResetDatabaseAsync();

        private CustomWebAppFactory _factory;

        private MessageResponse SignalRMessage = null;

        private HubConnection _connection;

        public MessageTests(CustomWebAppFactory factory)
        {
            _factory = factory;

            _connection = _factory.hubConnection;
            _connection.On<MessageResponse>("ReceiveMessage", message =>
            {
                SignalRMessage = message;
            });
        }

        private async Task InitTests()
        {
            SignalRMessage = null;

            await _factory.hubConnection.StartAsync();
        }

        private MessageRequest msgReq = new MessageRequest()
        {
            lon = 44.4,
            lat = 22.2,
            Body = "Test Message"
        };

        private PrivateMessageRequest privMsgReq = new PrivateMessageRequest
        {
            lon = 44.4,
            lat = 22.2,
            Body = "Test Message",
            MsgRecipientId = "DOG-id-123"
        };

        // Firebase will be tested front end

        [Fact]
        public async Task TEST_PublicReceiveMessageAndNotify_SignalRShouldRun()
        {
            await InitTests();

            var url = "api/chat/public";

            var jsonContent = JsonConvert.SerializeObject(msgReq);
            var content = new StringContent(jsonContent, Encoding.UTF8, "application/json");

            // Act
            var httpResponse = await _factory.HttpClient.PutAsync(url, content);
            await Task.Delay(500);

            Assert.NotNull(SignalRMessage);
            Assert.Equal(msgReq.Body, SignalRMessage.Body);
        }

        [Fact]
        public async Task TEST_PrivateReceiveMessageAndNotify_ShouldFail()
        {
            await InitTests();

            var url = "api/chat/private";

            var jsonContent = JsonConvert.SerializeObject(msgReq);
            var content = new StringContent(jsonContent, Encoding.UTF8, "application/json");

            var httpResponse = await _factory.HttpClient.PutAsync(url, content);
            await Task.Delay(500);

            Assert.Null(SignalRMessage);
            Assert.Equal(System.Net.HttpStatusCode.BadRequest, httpResponse.StatusCode);
        }

        [Fact]
        public async Task TEST_PrivateReceiveMessageAndNotify_SignalRShouldRun()
        {

            await InitTests();

            var url = "api/chat/private";

            var jsonContent = JsonConvert.SerializeObject(privMsgReq);
            var content = new StringContent(jsonContent, Encoding.UTF8, "application/json");

            var httpResponse = await _factory.HttpClient.PutAsync(url, content);
            await Task.Delay(500);

            Assert.NotNull(SignalRMessage);
            Assert.Equal(msgReq.Body, SignalRMessage.Body);
        }
    }
}
