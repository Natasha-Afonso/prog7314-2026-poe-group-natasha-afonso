using MetrixAPI.DTOs;
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
            [FromBody] UpdateLanguageDto dto)
        {
            return Ok(
                await _settingsService.UpdateLanguageAsync(dto));
        }

        [HttpPut("theme")]
        public async Task<IActionResult> UpdateTheme(
            [FromBody] UpdateThemeDto dto)
        {
            return Ok(
                await _settingsService.UpdateThemeAsync(dto));
        }

        [HttpPut("notifications")]
        public async Task<IActionResult> UpdateNotifications(
            [FromBody] UpdateNotificationsDto dto)
        {
            return Ok(
                await _settingsService.UpdateNotificationsAsync(dto));
        }

        [HttpPut("accessibility")]
        public async Task<IActionResult> UpdateAccessibility(
            [FromBody] UpdateAccessibilityDto dto)
        {
            return Ok(
                await _settingsService.UpdateAccessibilityAsync(dto));
        }

        [HttpPost("bugs")]
        public async Task<IActionResult> SubmitBug(
            [FromBody] BugReportDto dto)
        {
            if (string.IsNullOrWhiteSpace(dto.Title) ||
                string.IsNullOrWhiteSpace(dto.Description))
            {
                return BadRequest(
                    "Bug title and description are required.");
            }

            await _settingsService.SubmitBugAsync(dto);

            return Ok(new
            {
                message = "Bug report submitted successfully."
            });
        }

        [HttpPost("rating")]
        public async Task<IActionResult> SubmitRating(
            [FromBody] AppRatingDto dto)
        {
            await _settingsService.SubmitRatingAsync(dto);

            return Ok(new
            {
                message = "Thank you for rating MeTrix."
            });
        }
    }
}