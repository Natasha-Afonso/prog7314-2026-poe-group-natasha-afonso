using MetrixAPI.Data;
using MetrixAPI.Models;
using MetrixAPI.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace MetrixAPI.Controllers;

[ApiController]
[Route("api/calendar")]
[Authorize]
public class CalendarController : ControllerBase
{
    private readonly ApplicationDbContext _context;
    private readonly IUserIdentityService _identity;

    public CalendarController(ApplicationDbContext context, IUserIdentityService identity)
    {
        _context = context; _identity = identity;
    }

    [HttpGet]
    public async Task<ActionResult<List<CalendarEventDto>>> Get(int year, int month)
    {
        var userId = _identity.GetUserId();
        var first = new DateOnly(year, month, 1);
        var last = first.AddMonths(1).AddDays(-1);
        var result = new List<CalendarEventDto>();

        var schedules = await _context.OutfitSchedules
            .Where(x => x.UserId == userId && x.Date >= first && x.Date <= last)
            .Include(x => x.Outfit)
            .ToListAsync();
        result.AddRange(schedules.Select(x => new CalendarEventDto(x.Date, x.Outfit.Name, "Outfit", x.OutfitId, x.Outfit.Name)));

        var profile = await _context.Profiles.FirstOrDefaultAsync(x => x.UserId == userId);
        if (profile?.Birthday is DateOnly birthday && birthday.Month == month)
            result.Add(new CalendarEventDto(new DateOnly(year, month, birthday.Day), "Birthday", "Birthday", null, null));

        result.AddRange(SouthAfricanHolidays(year)
            .Where(x => x.Date >= first && x.Date <= last)
            .Select(x => new CalendarEventDto(x.Date, x.Title, "Holiday", null, null)));

        return result.OrderBy(x => x.Date).ToList();
    }

    private static IEnumerable<(DateOnly Date, string Title)> SouthAfricanHolidays(int year)
    {
        yield return (new DateOnly(year, 1, 1), "New Year's Day");
        yield return (new DateOnly(year, 3, 21), "Human Rights Day");
        yield return (new DateOnly(year, 4, 27), "Freedom Day");
        yield return (new DateOnly(year, 5, 1), "Workers' Day");
        yield return (new DateOnly(year, 6, 16), "Youth Day");
        yield return (new DateOnly(year, 8, 9), "National Women's Day");
        yield return (new DateOnly(year, 9, 24), "Heritage Day");
        yield return (new DateOnly(year, 12, 16), "Day of Reconciliation");
        yield return (new DateOnly(year, 12, 25), "Christmas Day");
        yield return (new DateOnly(year, 12, 26), "Day of Goodwill");

        var easter = EasterSunday(year);
        yield return (easter.AddDays(-2), "Good Friday");
        yield return (easter.AddDays(1), "Family Day");
    }

    private static DateOnly EasterSunday(int year)
    {
        var g = new DateTime(year, 1, 1).AddDays(0);
        var a = year % 19; var b = year / 100; var c = year % 100; var d = b / 4; var e = b % 4;
        var f = (b + 8) / 25; var gg = (b - f + 1) / 3; var h = (19 * a + b - d - gg + 15) % 30;
        var i = c / 4; var k = c % 4; var l = (32 + 2 * e + 2 * i - h - k) % 7; var m = (a + 11 * h + 22 * l) / 451;
        var month = (h + l - 7 * m + 114) / 31; var day = ((h + l - 7 * m + 114) % 31) + 1;
        return new DateOnly(year, month, day);
    }
}
