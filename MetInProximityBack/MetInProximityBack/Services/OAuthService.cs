using MetInProximityBack.Types;
using MetInProximityBack.Interfaces;
using System;
using Azure.Core;

namespace MetInProximityBack.Services
{
    public class OAuthService : IOAuthService
    {
        private readonly HttpClient _httpClient;
        private readonly IConfiguration _configuration;

        //dependency injection from program.cs
        public OAuthService(
            HttpClient httpClient,
            IConfiguration configuration
        )
        {
            _httpClient = httpClient;
            _configuration = configuration;
        }

        public async Task<OAuthTokenResponse> GetOAuthTokens(string url, Dictionary<string,string> req)
        {
            var content = new FormUrlEncodedContent(req);
            Console.WriteLine("in get oauth tokens");
            var response = await _httpClient.PostAsync(url, content);
            response.EnsureSuccessStatusCode();

            if (!response.IsSuccessStatusCode)
            {
                var errorContent = await response.Content.ReadAsStringAsync();
                throw new Exception("Failed to fetch OAuth ID_Tokenn: " + errorContent);
            }
            
            var tokens = await response.Content.ReadFromJsonAsync<OAuthTokenResponse>();

            return tokens;
        }

        /*
         * This may be neccessary later, not now tho cause we get user from Id_Token, Not through api resource call
        public async Task<HttpResponseMessage> GetUserAsResponse(
            string url,
            string accessToken
        ) {
            _httpClient.DefaultRequestHeaders.Authorization = new System.Net.Http.Headers.AuthenticationHeaderValue("Bearer", accessToken);


            HttpResponseMessage response = await _httpClient.GetAsync(url);

            if (!response.IsSuccessStatusCode)
            {
                var errorContent = await response.Content.ReadAsStringAsync();
                Console.WriteLine(errorContent);
            }
            //do i need this?
            response.EnsureSuccessStatusCode();

            return response;
        }
        */
    }
}
