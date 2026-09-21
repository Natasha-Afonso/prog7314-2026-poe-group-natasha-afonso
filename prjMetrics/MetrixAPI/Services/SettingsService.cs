// using MetrixAPI.Data;
// using MetrixAPI.DTOs;
// using MetrixAPI.Models;
// using Microsoft.EntityFrameworkCore;

// namespace MetrixAPI.Services
// {
//     public class SettingsService
//     {
//         private readonly ApplicationDbContext _context;
//         private readonly IUserIdentityService _identity;

//         public SettingsService(
//             ApplicationDbContext context,
//             IUserIdentityService identity)
//         {
//             _context = context;
//             _identity = identity;
//         }

//         public async Task<SettingsResponseDto> GetSettingsAsync()
//         {
//             var userId = _identity.GetUserId();

//             var settings = await GetOrCreateSettingsAsync(userId);

//             return new SettingsResponseDto
//             {
//                 Language = settings.Language,
//                 Theme = settings.Theme,
//                 PushNotifications = settings.PushNotifications,
//                 FontSize = settings.FontSize,
//                 HighContrast = settings.HighContrast
//             };
//         }

//         public async Task<SettingsResponseDto> UpdateLanguageAsync(
//             UpdateLanguageDto dto)
//         {
//             var settings =
//                 await GetOrCreateSettingsAsync(_identity.GetUserId());

//             settings.Language = dto.Language;
//             settings.UpdatedAt = DateTime.UtcNow;

//             await _context.SaveChangesAsync();

//             return await GetSettingsAsync();
//         }

//         public async Task<SettingsResponseDto> UpdateThemeAsync(
//             UpdateThemeDto dto)
//         {
//             var settings =
//                 await GetOrCreateSettingsAsync(_identity.GetUserId());

//             settings.Theme = dto.Theme;
//             settings.UpdatedAt = DateTime.UtcNow;

//             await _context.SaveChangesAsync();

//             return await GetSettingsAsync();
//         }

//         public async Task<SettingsResponseDto> UpdateNotificationsAsync(
//             UpdateNotificationsDto dto)
//         {
//             var settings =
//                 await GetOrCreateSettingsAsync(_identity.GetUserId());

//             settings.PushNotifications = dto.Enabled;
//             settings.UpdatedAt = DateTime.UtcNow;

//             await _context.SaveChangesAsync();

//             return await GetSettingsAsync();
//         }

//         public async Task<SettingsResponseDto> UpdateAccessibilityAsync(
//             UpdateAccessibilityDto dto)
//         {
//             if (dto.FontSize < 10 || dto.FontSize > 40)
//             {
//                 throw new ArgumentException(
//                     "Font size must be between 10 and 40.");
//             }

//             var settings =
//                 await GetOrCreateSettingsAsync(_identity.GetUserId());

//             settings.FontSize = dto.FontSize;
//             settings.HighContrast = dto.HighContrast;
//             settings.UpdatedAt = DateTime.UtcNow;

//             await _context.SaveChangesAsync();

//             return await GetSettingsAsync();
//         }

//         public async Task SubmitBugAsync(BugReportDto dto)
//         {
//             var bug = new BugReport
//             {
//                 UserId = _identity.GetUserId(),
//                 Title = dto.Title,
//                 Description = dto.Description
//             };

//             _context.BugReports.Add(bug);

//             await _context.SaveChangesAsync();
//         }

//         public async Task SubmitRatingAsync(AppRatingDto dto)
//         {
//             if (dto.Rating < 1 || dto.Rating > 5)
//             {
//                 throw new ArgumentException(
//                     "Rating must be between 1 and 5.");
//             }

//             var rating = new AppRating
//             {
//                 UserId = _identity.GetUserId(),
//                 Rating = dto.Rating,
//                 Comment = dto.Comment
//             };

//             _context.AppRatings.Add(rating);

//             await _context.SaveChangesAsync();
//         }

//         private async Task<UserSettings> GetOrCreateSettingsAsync(
//             string userId)
//         {
//             var settings = await _context.UserSettings
//                 .FirstOrDefaultAsync(x => x.UserId == userId);

//             if (settings != null)
//             {
//                 return settings;
//             }

//             settings = new UserSettings
//             {
//                 UserId = userId
//             };

//             _context.UserSettings.Add(settings);

//             await _context.SaveChangesAsync();

//             return settings;
//         }
//     }
// }