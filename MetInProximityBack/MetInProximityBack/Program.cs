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
using MetInProximityBack.Interfaces.IRepos;
using MetInProximityBack.Interfaces.IServices;
using MetInProximityBack.Services.Notifications;
using Microsoft.AspNetCore.RateLimiting;
using System.Threading.RateLimiting;
using MetInProximityBack.Types;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();


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

/* REMOVE THIS IN PRODUCTION */
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

/* Dont need this high lvl abstraction if using IDatabase
builder.Services.AddStackExchangeRedisCache(options =>
{
    options.Configuration = builder.Configuration.GetConnectionString("RedisConnectionString");
    options.InstanceName = "MetinInstance";
});
*/

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

builder.Logging.AddDebug();
builder.Logging.AddConsole();

builder.Services.AddIdentity<AppUser, IdentityRole>(options =>
{
    options.User.AllowedUserNameCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-._@+/ ";
    options.User.RequireUniqueEmail = true;

    options.Lockout.DefaultLockoutTimeSpan = TimeSpan.FromMinutes(5);
    options.Lockout.AllowedForNewUsers = true;
    options.Lockout.MaxFailedAccessAttempts = 3;
}).AddEntityFrameworkStores<AppDbContext>();

builder.Services.AddSignalR();
builder.Services.Configure<FirebaseConfig>(builder.Configuration.GetSection("FirebaseCM"));


builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAndroidApp",
        policy =>
        {
            policy.AllowAnyMethod()
                  .WithOrigins("http://localhost", "https://MetinProximity.com")
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

builder.Services.AddRateLimiter(options =>
{
    options.AddTokenBucketLimiter("chat", limiterOptions =>
    {
        limiterOptions.TokenLimit = 10; 
        limiterOptions.TokensPerPeriod = 5; 
        limiterOptions.ReplenishmentPeriod = TimeSpan.FromMinutes(1);
        limiterOptions.AutoReplenishment = true;
    });

    options.AddConcurrencyLimiter("signalr", limiterOptions =>
    {
        limiterOptions.PermitLimit = 25; 
        limiterOptions.QueueProcessingOrder = QueueProcessingOrder.OldestFirst;
        limiterOptions.QueueLimit = 5; 
    });

});


/* Configure this
builder.Services.AddHealthChecks()
    .AddRedis(config.GetConnectionString("Redis"))
    .AddAzureCosmosDb(config.GetConnectionString("CosmosDb"));

app.MapHealthChecks("/health");
*/

/* dependency injections*/
builder.Services.AddScoped<IOAuthService, OAuthService>();
builder.Services.AddScoped<AuthTokenService>();

builder.Services.AddScoped<IMessageService, MessageService>();
builder.Services.AddScoped<RedisCacheRepo>();
builder.Services.AddScoped<CosmoLocationRepo>();

builder.Services.AddScoped<INotificationService, NotificationService>();
builder.Services.AddScoped<FirebaseService>();
builder.Services.AddScoped<SignalRService>();

builder.Services.AddScoped<MapService>();

builder.Services.AddTransient<OAuthProviderFactory>();
builder.Services.AddTransient<IOAuthProvider, GoogleOAuthProvider>();
builder.Services.AddTransient<IOAuthProvider, MicrosoftOAuthProvider>();

/*
 following
https://learn.microsoft.com/en-us/dotnet/architecture/microservices/implement-resilient-applications/use-httpclientfactory-to-implement-resilient-http-requests
 */
builder.Services.AddHttpClient<IOAuthService, OAuthService>();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

app.UseAuthorization();

app.UseCors("AllowAndroidApp");

app.MapHub<ChatHub>("/chathub").RequireRateLimiting("signalr");

app.MapControllers();

app.UseRateLimiter();

app.Run();

// this here for testing :3
public partial class Program { }
