
using MetrixAPI.Data;
using Microsoft.Extensions.Options;
using Microsoft.EntityFrameworkCore;
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

            //Added Swagger generation services
            builder.Services.AddEndpointsApiExplorer();
            builder.Services.AddSwaggerGen();

            // Learn more about configuring OpenAPI at https://aka.ms/aspnet/openapi
            builder.Services.AddOpenApi();
            builder.Services.AddDbContext<ApplicationDbContext>(options => options.UseSqlServer(builder.Configuration.GetConnectionString("DefaultConnection")));
            builder.Services.AddHttpContextAccessor();
            builder.Services.AddScoped<UserIdentityService>();
            builder.Services.AddScoped<SettingsService>();

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
