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
            
            HttpResponseMessage response = await _httpClient.PostAsync(url, content);
            response.EnsureSuccessStatusCode();

            var tokens = await response.Content.ReadFromJsonAsync<OAuthTokenResponse>();

            return tokens;
        }

        public async Task<HttpResponseMessage> GetUserAsResponse(
            string url,
            string accessToken
        ) {
            _httpClient.DefaultRequestHeaders.Authorization = new System.Net.Http.Headers.AuthenticationHeaderValue("Bearer", accessToken);
            HttpResponseMessage response = await _httpClient.GetAsync(url);
            //do i need this?
            response.EnsureSuccessStatusCode();

            return response;
        }
    }
}
