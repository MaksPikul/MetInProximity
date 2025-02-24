using MetInProximityBack.Factories;
using MetInProximityBack.Interfaces;
using MetInProximityBack.Providers;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Xunit;

namespace Metin.UnitTests.FactoryTests
{
    public class OAuthProviderFactoryTest
    {
        private readonly OAuthProviderFactory _providerFactory;

        //Test Might need simplifing later
        public OAuthProviderFactoryTest()
        {
            var services = new ServiceCollection();

            var configuration = new ConfigurationBuilder()
                .AddInMemoryCollection(new Dictionary<string, string>
                {
                    { "GoogleOAuth:ClientId", "test" },
                    { "GoogleOAuth:ClientSecret", "test" },
                    { "GoogleOAuth:RedirectUri", "test" },
                    { "MicrosoftOAuthOAuth:ClientId", "test" },
                    { "MicrosoftOAuth:ClientId", "test" },
                    { "MicrosoftOAuth:RedirectUri", "test" },
                })
                .Build();

            services.AddSingleton<IConfiguration>(configuration);

            services.AddTransient<IOAuthProvider, GoogleOAuthProvider>();
            services.AddTransient<IOAuthProvider, MicrosoftOAuthProvider>();
            services.AddSingleton<OAuthProviderFactory>();

            var serviceProvider = services.BuildServiceProvider();

            _providerFactory = serviceProvider.GetRequiredService<OAuthProviderFactory>();
        }



        [Fact]
        public void TestGoogleProvider_IsValid()
        {
            var provider = _providerFactory.GetProvider("google");
            Assert.IsType<GoogleOAuthProvider>(provider);
        }

        [Fact]
        public void TestMicroSoftProvider_IsValid()
        {
            var provider = _providerFactory.GetProvider("microsoft");
            Assert.IsType<MicrosoftOAuthProvider>(provider);
        }

        [Fact]
        public void TestUnsupportedProvider_ThrowsEx()
        {
            var exception = Assert.Throws<ArgumentException>(() => _providerFactory.GetProvider("github"));
            Assert.Equal("Unsupported Provider", exception.Message);
        }
    }
}
