using Firebase.Auth;
using Firebase.Auth.Providers;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.Extensions.Configuration;
using Microsoft.IdentityModel.Tokens;
using MetrixAPI.Models;
using MetrixAPI.Services;

namespace MetrixAPI
{
    public class Program
    {
        public static void Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);

            // Add services to the container
            builder.Services.AddControllers();

            // Added Swagger generation services
            builder.Services.AddEndpointsApiExplorer();
            builder.Services.AddSwaggerGen();

            // Learn more about configuring OpenAPI at https://aka.ms/aspnet/openapi
            builder.Services.AddOpenApi();

            //configure firebase to map to the correct project id and key declared in appsettings
            var firebaseSettings = builder.Configuration.GetSection("Firebase").Get<FirebaseSettings>()
                ?? throw new InvalidOperationException( "Missing 'Firebase' configuration section");

            builder.Services.AddTransient(_ => new FirebaseAuthClient(new FirebaseAuthConfig
            {
                ApiKey = firebaseSettings.ApiKey,
                AuthDomain = $"{firebaseSettings.ProjectId}.firebaseapp.com",
                Providers = new FirebaseAuthProvider[] { new EmailProvider(), new GoogleProvider() }
            }));

            builder.Services.AddScoped<IAuthService, AuthService>();

            // Validate the Firebase ID tokens your AuthService issues.
            builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
                .AddJwtBearer(options => //JWT verifies the tokens
                {
                    options.Authority = $"https://securetoken.google.com/{firebaseSettings.ProjectId}";
                    options.MapInboundClaims = false; // keep claim names as Firebase sends them (e.g. "user_id", "email")
                    options.TokenValidationParameters = new TokenValidationParameters
                    {
                        ValidateIssuer = true,
                        ValidIssuer = $"https://securetoken.google.com/{firebaseSettings.ProjectId}",
                        ValidateAudience = true,
                        ValidAudience = firebaseSettings.ProjectId,
                        ValidateLifetime = true
                    };
                });

            builder.Services.AddAuthorization();

            var app = builder.Build();

            // Enable middleware to serve generated Swagger as a JSON endpoint for ALL environments.
            app.UseSwagger();

            // Enable middleware to serve swagger-ui for ALL environments.
            app.UseSwaggerUI();

            // Configure the HTTP request pipeline.
            if (app.Environment.IsDevelopment())
            {
                app.MapOpenApi();
            }

            //commenting this out because it was causing errors because  it expects https not http
            //app.UseHttpsRedirection();

            app.UseAuthentication();

            app.UseAuthorization();

            app.MapControllers();

            app.Run();
        }
    }
}