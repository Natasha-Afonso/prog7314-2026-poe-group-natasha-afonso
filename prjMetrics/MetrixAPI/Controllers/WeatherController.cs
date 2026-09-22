<<<<<<< Updated upstream
using System.Text.Json;
using MetrixAPI.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

=======
/*using System.Text.Json;
using MetrixAPI.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
>>>>>>> Stashed changes
namespace MetrixAPI.Controllers;

[ApiController]
[Route("api/weather")]
[Authorize]
public class WeatherController : ControllerBase
{
    private readonly HttpClient _http;
    public WeatherController(IHttpClientFactory factory) => _http = factory.CreateClient();

    [HttpGet]
    public async Task<ActionResult<WeatherDto>> Get(double latitude, double longitude)
    {
        var url = $"https://api.open-meteo.com/v1/forecast?latitude={latitude}&longitude={longitude}&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max&forecast_days=1&timezone=auto";
        using var response = await _http.GetAsync(url);
        if (!response.IsSuccessStatusCode) return StatusCode((int)response.StatusCode, "Weather service unavailable.");

        using var json = JsonDocument.Parse(await response.Content.ReadAsStringAsync());
        var daily = json.RootElement.GetProperty("daily");
        return Ok(new WeatherDto(
            daily.GetProperty("time")[0].GetString()!,
            daily.GetProperty("temperature_2m_max")[0].GetDouble(),
            daily.GetProperty("temperature_2m_min")[0].GetDouble(),
            daily.GetProperty("precipitation_probability_max")[0].GetDouble(),
            daily.GetProperty("weather_code")[0].GetInt32()));
    }
}
<<<<<<< Updated upstream
=======
*/
>>>>>>> Stashed changes
