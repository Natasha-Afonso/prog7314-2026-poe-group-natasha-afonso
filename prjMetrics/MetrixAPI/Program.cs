using Firebase.Auth;
using Firebase.Auth.Providers;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.Extensions.Configuration;
using Microsoft.IdentityModel.Tokens;
using MetrixAPI.Models;
using MetrixAPI.Services;

using MetrixAPI.Data;
using Microsoft.Extensions.Options;
using Microsoft.EntityFrameworkCore;
using MetrixAPI.Services;

namespace MetrixAPI
{
    //(TutorialsEU, 2022)
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
            builder.Services.AddDbContext<ApplicationDbContext>(options => options.UseSqlServer(builder.Configuration.GetConnectionString("DefaultConnection")));
            builder.Services.AddHttpContextAccessor();
            builder.Services.AddScoped<UserIdentityService>();
            builder.Services.AddScoped<SettingsService>();

            //configure firebase to map to the correct project id and key declared in appsettings
            var firebaseSettings = builder.Configuration.GetSection("Firebase").Get<FirebaseSettings>()
                ?? throw new InvalidOperationException("Missing 'Firebase' configuration section");

            //(Broderick, 2024)
            builder.Services.AddTransient(_ => new FirebaseAuthClient(new FirebaseAuthConfig
            {
                ApiKey = firebaseSettings.ApiKey,
                AuthDomain = $"{firebaseSettings.ProjectId}.firebaseapp.com",
                Providers = new FirebaseAuthProvider[] //enables the email and google providers
                { 
                    new EmailProvider(), 
                    new GoogleProvider() 
                }
            }));

            //(Watmore, 2018)
            builder.Services.AddScoped<IAuthService, AuthService>();

            //(Chandra, 2026)
            // Validate the Firebase ID tokens your AuthService issues.
            builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
                .AddJwtBearer(options => //JWT verifies the tokens
                {
                    options.Authority = $"https://securetoken.google.com/{firebaseSettings.ProjectId}";
                    options.MapInboundClaims = false; // keep claim names as Firebase sends them (e.g. "user_id", "email")
                    options.TokenValidationParameters = new TokenValidationParameters
                    { //(StackOverflow, 2018)
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

/*References
 
Broderick, P., 2024. Firebase Authentication in .NET (Version 10.0) [Source Code]. Available at: < https://code-maze.com/dotnet-firebase-authentication/ > [Accessed 17 September 2026]
Chandra, S., 2026. JWT Authentication in ASP.NET Core: A complete step-by-step guide. (Version 10.0) [Source Code]. Available at: < https://medium.com/@Rajdip27/jwt-authentication-in-asp-net-core-a-complete-step-by-step-guide-94c955d907da > [Accessed 17 September 2026]
StackOverlfow, 2018. Firebase Authentication (JWT) with .NET Core. (Version 10.0) [Source Code]. Available at: < https://stackoverflow.com/questions/42336950/firebase-authentication-jwt-with-net-core > [Accessed 17 September 2026]
TutorialsEU, 2022. Full stack developer course - using ASP.NET & Azure & C# & Android Jetpack Compose + Treblle. (Version 10.0) [Source Code]. Available at: < https://youtu.be/h1hwz_Y52po?si=fUPg4y2y9L8EuInT > [Accessed 17 September 2026]
Watmore, J., 2018. Aspnet core registration login api. (Version 10.0) [Source Code]. Available at: < https://github.com/cornflourblue/aspnet-core-registration-login-api/blob/master/Controllers/UsersController.cs > [Accessed 17 September 2026]

 */