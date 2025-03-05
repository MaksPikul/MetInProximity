using Azure.Core;
using System.Net.Http.Headers;
using Microsoft.Azure.Cosmos.Serialization.HybridRow;
using System.Net;
using System.Net.Http;
using System.Text;
using MetInProximityBack.Types.Message;
using MetInProximityBack.Models;
using Microsoft.AspNetCore.Identity;
using MetInProximityBack.Interfaces.IServices;
using Google.Apis.FirebaseCloudMessaging.v1;
using Google.Apis.FirebaseCloudMessaging.v1.Data;
using Google.Apis.Auth.OAuth2;
using Google.Apis.Services;
using MetInProximityBack.Types;
using Microsoft.Extensions.Options;

namespace MetInProximityBack.Services.Notifications
{
    public class FirebaseService {
        private readonly IHttpClientFactory _httpClientFactory;
        private readonly IConfiguration _config;
        private readonly UserManager<AppUser> _userManager;

        private readonly FirebaseCloudMessagingService _fcmService;

        public FirebaseService(
            IHttpClientFactory httpClientFactory,
            IConfiguration configuration,
            UserManager<AppUser> userManager,
            IOptions<FirebaseConfig> firebaseOptions
        )
        {
            var firebaseConfig = firebaseOptions.Value;

            var credential = GoogleCredential
                .FromJson(firebaseConfig.Credentials)
                .CreateScoped("https://www.googleapis.com/auth/firebase.messaging");

            _fcmService = new FirebaseCloudMessagingService(new BaseClientService.Initializer
            {
                HttpClientInitializer = credential
            });

            _httpClientFactory = httpClientFactory;
            _config = configuration;
            _userManager = userManager;
        }



        /*
         *  https://stackoverflow.com/questions/38184432/fcm-firebase-cloud-messaging-push-notification-with-asp-net
         */
        public async Task SendPushNotification(MessageResponse msgRes)
        {
            string fcmToken = await GetUserFcmToken(msgRes.RecipientId);

            var message = new Message
            {
                Token = fcmToken,
                Notification = new Notification
                {
                    Title = "Metin Message",
                    Body = msgRes.Body
                }
            };

            var request = new SendMessageRequest { Message = message };
            var response = await _fcmService.Projects.Messages.Send(request, $"projects/{_config["FirebaseCM:ProjectId"]}").ExecuteAsync();
        }

        public async Task SendPushNotification(string recipientId, MessageResponse msgRes)
        {
            string fcmSecretKey = _config["Firebase:SecretKey"];
            string fcmUrl = _config["Firebase:Url"];
            string fcmToken = await GetUserFcmToken(recipientId);

            // correct usage of Http Client
            HttpClient _httpClient = _httpClientFactory.CreateClient();

            _httpClient.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", $"key={fcmSecretKey}");

            /*
             * https://firebase.google.com/docs/cloud-messaging/send-message#send_using_the_fcm_v1_http_api
             */
            var payload = CreateFcmPayload(fcmToken, msgRes);

            var jsonPayload = Newtonsoft.Json.JsonConvert.SerializeObject(payload);

            var content = new StringContent(jsonPayload, Encoding.UTF8, "application/json");

            var response = await _httpClient.PostAsync(fcmUrl, content);

            if (response.IsSuccessStatusCode)
            {
                return;
            }
            else
            {
                throw new Exception("HttpClient failed to post message to firebase, StatusCode: " + response.StatusCode);
            }
        }

        private FcmPayload CreateFcmPayload(string fcmToken, MessageResponse msgRes)
        {
            var payload = new FcmPayload(fcmToken, msgRes);

            return payload;
        }

        private async Task<string> GetUserFcmToken(string userId)
        {

            AppUser user = await _userManager.FindByIdAsync(userId);

            return "s";//user.FcmToken;
        }



    }

}

