using MetrixAPI.Controllers;
using MetrixAPI.Data;
using MetrixAPI.Models;
using Microsoft.EntityFrameworkCore;

namespace MetrixAPI.Services
{
    public class SettingsService
    {
        private readonly ApplicationDbContext _context;
        private readonly IUserIdentityService _identity;

        public SettingsService(
            ApplicationDbContext context,
            IUserIdentityService identity)
        {
            _context = context;
            _identity = identity;
        }

        public async Task<UserSettings> GetSettingsAsync()
        {
            var userId = _identity.GetUserId();

            return await GetOrCreateSettingsAsync(userId);

        }

        public async Task<UserSettings> UpdateLanguageAsync(
            string language)
        {
            var settings =
                await GetOrCreateSettingsAsync(_identity.GetUserId());

            settings.Language = language;
            settings.UpdatedAt = DateTime.UtcNow;

            await _context.SaveChangesAsync();

            return settings;
        }

        public async Task<UserSettings> UpdateThemeAsync(
            string theme)
        {
            var settings =
                await GetOrCreateSettingsAsync(_identity.GetUserId());

            settings.Theme = theme;
            settings.UpdatedAt = DateTime.UtcNow;

            await _context.SaveChangesAsync();

            return settings;
        }

        public async Task<UserSettings> UpdateNotificationsAsync(
            bool enabled)
        {
            var settings =
                await GetOrCreateSettingsAsync(_identity.GetUserId());

            settings.PushNotifications = enabled;
            settings.UpdatedAt = DateTime.UtcNow;

            await _context.SaveChangesAsync();

            return settings;
        }

        public async Task<UserSettings> UpdateAccessibilityAsync(
            AccessibilitySettings settingsRequest)
        {
            var validSizes = new []
            {
                "Small",
                "Medium",
                "Large"
            };

            if (!validSizes.Contains(settingsRequest.FontSize))
            {
              throw new ArgumentException("Font size must be small, medium or large.");
            }

            var settings =
                await GetOrCreateSettingsAsync(_identity.GetUserId());

            settings.FontSize = settingsRequest.FontSize;
            settings.HighContrast = settingsRequest.HighContrast;
            settings.UpdatedAt = DateTime.UtcNow;

            await _context.SaveChangesAsync();

            return settings;
        }

        public async Task SubmitBugAsync(string title, string description)
        {
            var bug = new BugReport
            {
                UserId = _identity.GetUserId(),
                Title = title,
                Description = description
            };

            _context.BugReports.Add(bug);

            await _context.SaveChangesAsync();
        }

        public async Task SubmitRatingAsync(int rating, string? comment)
        {
            if (rating < 1 || rating > 5)
            {
                throw new ArgumentException(
                    "Rating must be between 1 and 5.");
            }

            var appRating = new AppRating
            {
                UserId = _identity.GetUserId(),
                Rating = rating,
                Comment = comment
            };

            _context.AppRatings.Add(appRating);

            await _context.SaveChangesAsync();
        }

        private async Task<UserSettings> GetOrCreateSettingsAsync(
            string userId)
        {
            var settings = await _context.UserSettings
                .FirstOrDefaultAsync(x => x.UserId == userId);

             if (settings != null)
            {
                 return settings;
           }

             settings = new UserSettings
             {
                UserId = userId
            };

             _context.UserSettings.Add(settings);

             await _context.SaveChangesAsync();

             return settings;
     }
    }
 }