using MetrixAPI.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace MetrixAPI.Controllers
{
    [ApiController]
    [Route("api/settings")]
    [Authorize]
    public class SettingsController : ControllerBase
    {
        private readonly SettingsService _settingsService;

        public SettingsController(SettingsService settingsService)
        {
            _settingsService = settingsService;
        }

        [HttpGet]
        public async Task<IActionResult> GetSettings()
        {
            return Ok(await _settingsService.GetSettingsAsync());
        }

        [HttpPut("language")]
        public async Task<IActionResult> UpdateLanguage(
            [FromBody] string language)
        {
            return Ok(
                await _settingsService.UpdateLanguageAsync(language));
        }

        [HttpPut("theme")]
        public async Task<IActionResult> UpdateTheme(
            [FromBody] string theme)
        {
            return Ok(
                await _settingsService.UpdateThemeAsync(theme));
        }

        [HttpPut("notifications")]
        public async Task<IActionResult> UpdateNotifications(
            [FromBody] bool enabled)
        {
            return Ok(
                await _settingsService.UpdateNotificationsAsync(enabled));
        }

        [HttpPut("accessibility")]
        public async Task<IActionResult> UpdateAccessibility(
            [FromBody] AccessibilitySettings settings)
        {
            return Ok(
                await _settingsService.UpdateAccessibilityAsync(settings));
        }

        [HttpPost("bugs")]
        public async Task<IActionResult> SubmitBug(
            [FromBody] BugReportRequest request)
        {
            if (string.IsNullOrWhiteSpace(request.Title) ||
                string.IsNullOrWhiteSpace(request.Description))
            {
                return BadRequest(
                    "Bug title and description are required.");
            }

            await _settingsService.SubmitBugAsync(request.Title, request.Description);

            return Ok(new
            {
                message = "Bug report submitted successfully."
            });
        }

        [HttpPost("rating")]
        public async Task<IActionResult> SubmitRating(
            [FromBody] RatingRequest request)
        {
            await _settingsService.SubmitRatingAsync(request.Rating, request.Comment);

            return Ok(new
            {
                message = "Thank you for rating MeTrix."
            });
        }
    }

    public class AccessibilitySettings
    {
        public string FontSize {get; set;} = "Medium";
        public bool HighContrast {get; set;}
    }

    public class BugReportRequest
    {
        public string Title {get; set;} = string.Empty;
        public string Description {get; set;} = string.Empty;
    }

    public class RatingRequest
    {
        public int Rating {get; set;}
        public string? Comment {get; set;}
    }
}