using System;
using Azure.Core;
using MetInProximityBack.Types.OAuth;
using MetInProximityBack.Interfaces.IServices;

namespace MetInProximityBack.Services
{
    [Obsolete("Class is unneccassry, this functionality is fufilled in the android app")]
    // Put alot of work and thought into this, feel bad deleting 
    public class OAuthService //: IOAuthService
    {
        //private readonly HttpClient _httpClient;
        private readonly IConfiguration _configuration;

        //dependency injection from program.cs
        public OAuthService(
            //HttpClient httpClient,
            IConfiguration configuration
        )
        {
            //_httpClient = httpClient;
            _configuration = configuration;
        }
        /*
        public async Task<OAuthTokenResponse> GetOAuthTokens(string url, Dictionary<string,string> req)
        {
            var content = new FormUrlEncodedContent(req);

            var response = await _httpClient.PostAsync(url, content);

            if (!response.IsSuccessStatusCode)
            {
                var errorContent = await response.Content.ReadAsStringAsync();
                throw new Exception("Failed to fetch OAuth ID_Token: " + errorContent);
            }
            
            var tokens = await response.Content.ReadFromJsonAsync<OAuthTokenResponse>();

            return tokens;
        }
       
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
