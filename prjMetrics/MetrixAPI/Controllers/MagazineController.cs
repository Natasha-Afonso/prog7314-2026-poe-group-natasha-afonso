using MetrixAPI.Data;
using MetrixAPI.Models;
using MetrixAPI.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace MetrixAPI.Controllers;

[ApiController]
[Route("api/magazine")]
[Authorize]
public class MagazineController : ControllerBase
{
    private readonly ApplicationDbContext _context;
    private readonly IUserIdentityService _identity;
    public MagazineController(ApplicationDbContext context, IUserIdentityService identity) { _context = context; _identity = identity; }

    [HttpGet("weekly")]
    public async Task<ActionResult<MagazineDto>> Weekly([FromQuery] DateOnly? weekEnd)
    {
        var userId = _identity.GetUserId();
        var end = weekEnd ?? DateOnly.FromDateTime(DateTime.UtcNow);
        var start = end.AddDays(-6);

       var goals = await _context.HabitLogs
    .Where(log =>
        log.LogDate >= start &&
        log.LogDate <= end &&
        log.Progress >= 1)
    .Join(
        _context.Habits,
        log => log.HabitId,
        habit => habit.Id,
        (log, habit) => new { log, habit }
    )
    .CountAsync(x => x.habit.UserId == userId);
        var journals = await _context.Journal
        .CountAsync(x => 
        x.UserId == userId && x.CreatedAt.Date >= start
        .ToDateTime(TimeOnly.MinValue)
        .Date && x.CreatedAt.Date <= end
        .ToDateTime(TimeOnly.MaxValue)
        .Date);

        var wears = await _context
        .OutfitWearLogs
        .Where(x => x.UserId == userId && x.Date >= start && x.Date <= end)
        .Include(x => x.Outfit).ToListAsync();
        var accepted = wears
        .Count(x => x.AcceptedSuggestion);
        var rejected = wears.Count(x => !x.AcceptedSuggestion);
        var favourite = wears
        .GroupBy(x => x.Outfit.Name)
        .OrderByDescending(g => g.Count())
        .Select(g => g.Key)
        .FirstOrDefault();
        var latestMood = await _context.Journal
        .Where(x => x.UserId == userId)
        .OrderByDescending(x => x.CreatedAt)
        .Select(x => x.Mood)
        .FirstOrDefaultAsync();
        var reflection = await _context.SelfCareQuizResults
        .Where(x => x.UserId == userId && x.CompletedAt.Date >= start
        .ToDateTime(TimeOnly.MinValue).Date && x.CompletedAt.Date <= end.ToDateTime(TimeOnly.MaxValue).Date)
        .OrderByDescending(x => x.CompletedAt).Select(x => x.Reflection)
        .FirstOrDefaultAsync();

        return Ok(new MagazineDto(start, end, goals, journals, wears.Count, accepted, rejected, favourite, latestMood, reflection));
    }
}
