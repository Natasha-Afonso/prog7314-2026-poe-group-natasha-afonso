using MetrixAPI.Data;
using MetrixAPI.Models;
using MetrixAPI.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace MetrixAPI.Controllers;

[ApiController]
[Route("api/outfits")]
[Authorize]
public class OutfitController : ControllerBase
{
    private readonly ApplicationDbContext _db;
    private readonly IUserIdentityService _identity;

    public OutfitController(ApplicationDbContext db, IUserIdentityService identity)
    {
        _db = db;
        _identity = identity;
    }

    [HttpGet]
    public async Task<ActionResult<List<OutfitDto>>> Get()
    {
        var userId = _identity.GetUserId();
        var outfits = await _db.Outfits
            .Where(o => o.UserId == userId)
            .Include(o => o.Items).ThenInclude(i => i.ClothingItem)
            .OrderByDescending(o => o.CreatedAt)
            .ToListAsync();

        return outfits.Select(ToDto).ToList();
    }

    [HttpPost]
    public async Task<ActionResult<OutfitDto>> Create(CreateOutfitRequest request)
    {
        var userId = _identity.GetUserId();
        if (string.IsNullOrWhiteSpace(request.Name) || request.ClothingItemIds.Count == 0)
            return BadRequest("An outfit name and at least one clothing item are required.");

        var ids = request.ClothingItemIds.Distinct().ToList();
        var owned = await _db.ClothingItems
            .Where(c => c.UserId == userId && !c.IsWishlist && ids.Contains(c.Id))
            .ToListAsync();

        if (owned.Count != ids.Count)
            return BadRequest("One or more clothing items are not owned by the current user.");

        var outfit = new Outfit { UserId = userId, Name = request.Name.Trim() };
        foreach (var item in owned)
            outfit.Items.Add(new OutfitItem { ClothingItemId = item.Id });

        _db.Outfits.Add(outfit);
        await _db.SaveChangesAsync();

        await _db.Entry(outfit).Collection(x => x.Items).Query().Include(x => x.ClothingItem).LoadAsync();
        return Ok(ToDto(outfit));
    }

    [HttpPost("schedule")]
    public async Task<IActionResult> Schedule(ScheduleOutfitRequest request)
    {
        var userId = _identity.GetUserId();
        var outfit = await _db.Outfits.FirstOrDefaultAsync(o => o.Id == request.OutfitId && o.UserId == userId);
        if (outfit == null) return NotFound("Outfit not found.");

        var existing = await _db.OutfitSchedules.FirstOrDefaultAsync(x => x.UserId == userId && x.Date == request.Date);
        if (existing == null)
            _db.OutfitSchedules.Add(new OutfitSchedule { UserId = userId, OutfitId = request.OutfitId, Date = request.Date });
        else
            existing.OutfitId = request.OutfitId;

        await _db.SaveChangesAsync();
        return Ok();
    }

    [HttpGet("schedule")]
    public async Task<ActionResult<List<CalendarEventDto>>> Schedule([FromQuery] DateOnly? from, [FromQuery] DateOnly? to)
    {
        var userId = _identity.GetUserId();
        var start = from ?? DateOnly.FromDateTime(DateTime.UtcNow);
        var end = to ?? start.AddMonths(1);
        var schedules = await _db.OutfitSchedules
            .Where(x => x.UserId == userId && x.Date >= start && x.Date <= end)
            .Include(x => x.Outfit)
            .OrderBy(x => x.Date)
            .ToListAsync();

        return schedules.Select(x => new CalendarEventDto(x.Date, x.Outfit.Name, "Outfit", x.OutfitId, x.Outfit.Name)).ToList();
    }

    [HttpPost("wear-log")]
    public async Task<IActionResult> WearLog(WearOutfitRequest request)
    {
        var userId = _identity.GetUserId();
        var outfit = await _db.Outfits.AnyAsync(o => o.Id == request.OutfitId && o.UserId == userId);
        if (!outfit) return NotFound("Outfit not found.");

        var date = request.Date ?? DateOnly.FromDateTime(DateTime.UtcNow);
        _db.OutfitWearLogs.Add(new OutfitWearLog
        {
            UserId = userId,
            OutfitId = request.OutfitId,
            Date = date,
            AcceptedSuggestion = request.AcceptedSuggestion
        });
        await _db.SaveChangesAsync();
        return Ok();
    }

    //DELETE api/outfits/5, removes the outfit
    [HttpDelete("{id:int}")]
    public async Task<IActionResult> DeleteOutfit(int id)
    {
        var userId = _identity.GetUserId();
        var outfit = await _db.Outfits.FirstOrDefaultAsync(o => o.Id == id && o.UserId == userId);
        if (outfit == null) return NotFound("Outfit not found.");

        _db.OutfitSchedules.RemoveRange(await _db.OutfitSchedules.Where(x => x.OutfitId == id && x.UserId == userId).ToListAsync());
        _db.OutfitWearLogs.RemoveRange(await _db.OutfitWearLogs.Where(x => x.OutfitId == id && x.UserId == userId).ToListAsync());
        _db.OutfitItems.RemoveRange(await _db.OutfitItems.Where(x => x.OutfitId == id).ToListAsync());
        _db.Outfits.Remove(outfit);

        await _db.SaveChangesAsync();
        return NoContent();
    }

    //DELETE api/outfits/schedule/2026-10-23, clears the outfit planned for that day
    [HttpDelete("schedule/{date}")]
    public async Task<IActionResult> Unschedule(DateOnly date)
    {
        var userId = _identity.GetUserId();
        var rows = await _db.OutfitSchedules.Where(x => x.UserId == userId && x.Date == date).ToListAsync();
        _db.OutfitSchedules.RemoveRange(rows);
        await _db.SaveChangesAsync();
        return NoContent();
    }

    //GET api/outfits/wear-log?from=2026-10-19&to=2026-10-25,  what the user wore used by the weekly magazine
    [HttpGet("wear-log")]
    public async Task<ActionResult<List<WearLogDto>>> GetWearLogs([FromQuery] DateOnly? from, [FromQuery] DateOnly? to)
    {
        var userId = _identity.GetUserId();
        var today = DateOnly.FromDateTime(DateTime.UtcNow);
        var start = from ?? today.AddDays(-7);
        var end = to ?? today;

        var logs = await _db.OutfitWearLogs
            .Where(x => x.UserId == userId && x.Date >= start && x.Date <= end)
            .Include(x => x.Outfit).ThenInclude(o => o.Items).ThenInclude(i => i.ClothingItem)
            .OrderBy(x => x.Date)
            .ToListAsync();

        return logs.Select(x => new WearLogDto(
            x.Date,
            x.OutfitId,
            x.Outfit.Name,
            x.AcceptedSuggestion,
            x.Outfit.Items
                .Select(i => new OutfitItemDto(i.ClothingItemId, i.ClothingItem.Name, i.ClothingItem.Category, i.ClothingItem.ImageUrl))
                .ToList())).ToList();
    }

    //DELETE api/outfits/wear-log/2026-10-23, the user changed their mind about wearing the suggestion
    [HttpDelete("wear-log/{date}")]
    public async Task<IActionResult> DeleteWearLog(DateOnly date)
    {
        var userId = _identity.GetUserId();
        var rows = await _db.OutfitWearLogs.Where(x => x.UserId == userId && x.Date == date).ToListAsync();
        _db.OutfitWearLogs.RemoveRange(rows);
        await _db.SaveChangesAsync();
        return NoContent();
    }

    private static OutfitDto ToDto(Outfit outfit) =>
        new(outfit.Id, outfit.Name, outfit.CreatedAt,
            outfit.Items.Select(i => new OutfitItemDto(i.ClothingItemId, i.ClothingItem.Name, i.ClothingItem.Category, i.ClothingItem.ImageUrl)).ToList());
}