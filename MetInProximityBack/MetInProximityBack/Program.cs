using MetInProximityBack.Data;
using MetInProximityBack.Factories;
using MetInProximityBack.Interfaces;
using MetInProximityBack.Models;
using MetInProximityBack.Services;
using MetInProximityBack.Providers;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using Microsoft.Azure.Cosmos;
using StackExchange.Redis;
using MetInProximityBack.Hubs;
using MetInProximityBack.Services.Tokens;
using Microsoft.OpenApi.Models;
using MetInProximityBack.Repositories;
using MetInProximityBack.Interfaces.IServices;
using MetInProximityBack.Services.Notifications;
using MetInProximityBack.Constants;
using FirebaseAdmin;
using Google.Apis.Auth.OAuth2;
using System.Text.Json;
using MetInProximityBack.Types.Location;
using MetInProximityBack.Interfaces.IRepos;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();

builder.Services.AddHttpClient();

builder.Logging.ClearProviders();
builder.Logging.AddConsole();
builder.Logging.AddDebug();

// This is for testing APIs :)
builder.Services.AddSwaggerGen(option =>
{
    option.SwaggerDoc("v1", new OpenApiInfo { Title = "Demo API", Version = "v1" });
    option.AddSecurityDefinition("Bearer", new OpenApiSecurityScheme
    {
        In = ParameterLocation.Header,
        Description = "Please enter a valid token",
        Name = "Authorization",
        Type = SecuritySchemeType.Http,
        BearerFormat = "JWT",
        Scheme = "Bearer"
    });
    option.AddSecurityRequirement(new OpenApiSecurityRequirement
    {
        {
            new OpenApiSecurityScheme
            {
                Reference = new OpenApiReference
                {
                    Type=ReferenceType.SecurityScheme,
                    Id="Bearer"
                }
            },
            new string[]{}
        }
    });
});

CosmosClientOptions options = new()
{
    HttpClientFactory = () => new HttpClient(new HttpClientHandler()
    {
        ServerCertificateCustomValidationCallback = HttpClientHandler.DangerousAcceptAnyServerCertificateValidator
    }),
    ConnectionMode = ConnectionMode.Gateway,
};

builder.Services.AddDbContext<AppDbContext>(options =>
{
    options.UseSqlServer(builder.Configuration.GetConnectionString("DefaultConnection"));
});

builder.Services.AddSingleton<CosmosClient>(sp =>
    new CosmosClient(
        builder.Configuration.GetConnectionString("CosmosAccountEndpoint"),
        builder.Configuration["CosmosDb:AuthKey"],
        clientOptions: options
    )
);
builder.Services.AddSingleton<CosmoLocationRepo>(sp =>
    new CosmoLocationRepo(
        sp.GetRequiredService<CosmosClient>()
    )
);

builder.Services.AddSingleton<IConnectionMultiplexer>(sp =>
{
    var configuration = builder.Configuration.GetConnectionString("RedisConnectionString");

    var redis = ConnectionMultiplexer.Connect(configuration);
    return redis;

});
builder.Services.AddScoped<IDatabase>(sp =>
{
    var multiplexer = sp.GetRequiredService<IConnectionMultiplexer>();
    var db = multiplexer.GetDatabase();
    return db;
    
});

builder.Services.AddIdentity<AppUser, IdentityRole>(options =>
{
    options.User.AllowedUserNameCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-._@+/ ";
    options.User.RequireUniqueEmail = true;

    options.Lockout.DefaultLockoutTimeSpan = TimeSpan.FromMinutes(5);
    options.Lockout.AllowedForNewUsers = true;
    options.Lockout.MaxFailedAccessAttempts = 3;
}).AddEntityFrameworkStores<AppDbContext>();

// Websockets Init
builder.Services.AddSignalR();

// Firebase Init (For Notifications)

var fbConfigSection = builder.Configuration.GetSection("FirebaseConfig");

var fbConfigJson = JsonSerializer.Serialize(fbConfigSection.Get<Dictionary<string, string>>());

var credential = GoogleCredential
    .FromJson(fbConfigJson)
    .CreateScoped("https://www.googleapis.com/auth/firebase.messaging");

FirebaseApp.Create(
    new AppOptions
    {
        Credential = credential
    }
);


builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAndroidApp",
        policy =>
        {
            policy
                .AllowAnyMethod()
                .WithOrigins("http://10.0.2.2", "https://MetinProximity.com")
                .AllowAnyHeader()
                .AllowCredentials();
        });
});

builder.Services.AddAuthentication(options =>
{
    options.DefaultAuthenticateScheme =
    options.DefaultChallengeScheme =
    options.DefaultForbidScheme =
    options.DefaultScheme =
    options.DefaultSignInScheme =
    options.DefaultSignOutScheme = JwtBearerDefaults.AuthenticationScheme;
}).AddJwtBearer(options =>
{
    options.TokenValidationParameters = new TokenValidationParameters
    {
        ValidateIssuer = true,
        ValidIssuer = builder.Configuration["JWT:Issuer"],
        ValidateAudience = true,
        ValidAudience = builder.Configuration["JWT:Audience"],
        ValidateIssuerSigningKey = true,
        IssuerSigningKey = new SymmetricSecurityKey(
            System.Text.Encoding.UTF8.GetBytes(builder.Configuration["JWT:SigningKey"])
        )
    };
});

/* dependency injections*/
//builder.Services.AddScoped<IOAuthService, OAuthService>();
builder.Services.AddSingleton<AuthTokenService>();

builder.Services.AddScoped<IMessageService, MessageService>();
builder.Services.AddScoped<ICacheRepo, RedisCacheRepo>();
builder.Services.AddScoped<IDocumentRepo, CosmoLocationRepo>();

builder.Services.AddScoped<INotificationService, NotificationService>();
builder.Services.AddScoped<IPushNotifService, FirebaseService>();
builder.Services.AddScoped<IWebSocketService, SignalRService>();

builder.Services.AddScoped<IMapService, MapService>();

builder.Services.AddSingleton<OAuthProviderFactory>();
builder.Services.AddTransient<IOAuthProvider, GoogleOAuthProvider>();
builder.Services.AddTransient<IOAuthProvider, MicrosoftOAuthProvider>();

var app = builder.Build();

app.UseSwagger();
app.UseSwaggerUI();


// Migrates SqlEdge to contain Identity Tables on start up
// "person checking my app wont need to run anything to start testing"
// No need for dummy users, tester should log in with provided google account, and use provided jwts to log into swagger and act as a second phone 
// (send api calls to web server directly instead of from client to server)
using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
    await db.Database.MigrateAsync();  
}

using (var scope = app.Services.CreateScope())
{

    var cosmosClient = scope.ServiceProvider.GetRequiredService<CosmosClient>();
    Console.WriteLine("creating dbs");
    var databaseResponse = await cosmosClient.CreateDatabaseIfNotExistsAsync(AppConstants.COSMO_LOC_DB);
    Console.WriteLine("creating container");
    var containerResponse = await databaseResponse.Database.CreateContainerIfNotExistsAsync(
        AppConstants.COSMO_LOC_CON, AppConstants.COSMO_PART_KEY, 400
    );
    
    if (containerResponse.StatusCode == System.Net.HttpStatusCode.OK)
    {
        Console.WriteLine("CosmoDb (NoSql) container created");
    }
    else
    {
        Console.WriteLine("There has been an error creating container.");
    }
}

app.UseHttpsRedirection();

app.UseAuthorization();

app.UseCors("AllowAndroidApp");

app.MapHub<ChatHub>("/chathub");

app.MapControllers();

app.Run();

// this here for testing :3
public partial class Program { }
