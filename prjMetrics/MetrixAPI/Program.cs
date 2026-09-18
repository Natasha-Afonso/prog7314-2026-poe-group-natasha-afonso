
using Firebase.Auth;
using Firebase.Auth.Providers;

namespace MetrixAPI
{
    public class Program
    {
        public static void Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);

            // Add services to the container
            builder.Services.AddControllers();

            //Added Swagger generation services
            builder.Services.AddEndpointsApiExplorer();
            builder.Services.AddSwaggerGen();

            // Learn more about configuring OpenAPI at https://aka.ms/aspnet/openapi
            builder.Services.AddOpenApi();

            //configure the firebase API key
            builder.Services.AddTransient(_ => FirebaseAuthClient(new FirebaseAuthConfig
            {
                ApiKey = firebase.ApiKey,
                AuthDomain = $"{Firebase.ProjectId}.firebaseapp.com",
                Providers = [new EmailProvider(), new GoogleProvider()]
            }));

            var app = builder.Build();

            //Enable middleware to serve generated Swagger as a JSON endpoint for ALL environments.
            app.UseSwagger();

            //Enable middleware to serve swagger-ui for ALL environments.
            app.UseSwaggerUI();

            // Configure the HTTP request pipeline.
            if (app.Environment.IsDevelopment())
            {
                app.MapOpenApi();
            }

            app.UseHttpsRedirection();

            app.UseAuthorization();


            app.MapControllers();

            app.Run();
        }
    }
}
