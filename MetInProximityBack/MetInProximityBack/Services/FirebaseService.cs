using Azure.Core;
using System.Net.Http.Headers;
using MetInProximityBack.Interfaces;
using Microsoft.Azure.Cosmos.Serialization.HybridRow;
using System.Net;
using System.Net.Http;
using System.Text;
using MetInProximityBack.Types.Message;
using MetInProximityBack.Models;
using Microsoft.AspNetCore.Identity;

namespace MetInProximityBack.Services
{
    public class FirebaseService(
        IHttpClientFactory httpClientFactory,
        IConfiguration configuration,
        UserManager<AppUser> userManager

    ) : INotificationService
    {
        private readonly IHttpClientFactory _httpClientFactory = httpClientFactory;
        private readonly IConfiguration _config = configuration;
        private readonly UserManager<AppUser> _userManager = userManager;

        /*
         *  https://stackoverflow.com/questions/38184432/fcm-firebase-cloud-messaging-push-notification-with-asp-net
         */
        public async void SendPushNotification(string recipientId, MessageResponse msgRes)
        {
            string fcmSecretKey = _config["Firebase:SecretKey"];
            string fcmUrl = _config["Firebase:Url"];
            string fcmToken = await this.GetUserFcmToken(recipientId);

            // correct usage of Http Client
            HttpClient _httpClient = _httpClientFactory.CreateClient();

            _httpClient.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", $"key={fcmSecretKey}");

            /*
             * https://firebase.google.com/docs/cloud-messaging/send-message#send_using_the_fcm_v1_http_api
             */
            var payload = this.CreateFcmPayload(fcmToken, msgRes);

            var jsonPayload = Newtonsoft.Json.JsonConvert.SerializeObject(payload);

            var content = new StringContent(jsonPayload, Encoding.UTF8, "application/json");

            var response = await _httpClient.PostAsync(fcmUrl, content);

            if (response.IsSuccessStatusCode)
            {
                return;
            }
            else
            {
                throw new Exception("HttpClient failed to post message to firebase, StatusCode: " + response.StatusCode );
            }
        }

        private FcmPayload CreateFcmPayload(string fcmToken, MessageResponse msgRes){

            var payload = new FcmPayload(fcmToken, msgRes);

            return payload;
        }

        private async Task<string> GetUserFcmToken(string userId){

            AppUser user = await _userManager.FindByIdAsync(userId);

            return user.FcmToken;
        }

            

    }

}

