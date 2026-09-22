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

    private readonly ApplicationDbContext _context;
    private readonly IUserIdentityService _identity;

    public OutfitController(ApplicationDbContext context, IUserIdentityService identity)
    {
        _context = context;
        _identity = identity;
    }


    [HttpGet]
    public async Task<ActionResult<List<OutfitDto>>> Get()
    {
        var userId = _identity.GetUserId();
        var outfits = await _context.Outfits
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
        var owned = await _context.ClothingItems
            .Where(c => c.UserId == userId && !c.IsWishlist && ids.Contains(c.Id))
            .ToListAsync();

        if (owned.Count != ids.Count)
            return BadRequest("One or more clothing items are not owned by the current user.");

        var outfit = new Outfit { UserId = userId, Name = request.Name.Trim() };
        foreach (var item in owned)
            outfit.Items.Add(new OutfitItem { ClothingItemId = item.Id });

        _context.Outfits.Add(outfit);
        await _context.SaveChangesAsync();

        await _context.Entry(outfit).Collection(x => x.Items).Query().Include(x => x.ClothingItem).LoadAsync();
        return Ok(ToDto(outfit));
    }

    [HttpPost("schedule")]
    public async Task<IActionResult> Schedule(ScheduleOutfitRequest request)
    {
        var userId = _identity.GetUserId();
        var outfit = await _context.Outfits.FirstOrDefaultAsync(o => o.Id == request.OutfitId && o.UserId == userId);
        if (outfit == null) return NotFound("Outfit not found.");

        var existing = await _context.OutfitSchedules.FirstOrDefaultAsync(x => x.UserId == userId && x.Date == request.Date);
        if (existing == null)
            _context.OutfitSchedules.Add(new OutfitSchedule { UserId = userId, OutfitId = request.OutfitId, Date = request.Date });
        else
            existing.OutfitId = request.OutfitId;

        await _context.SaveChangesAsync();
        return Ok();
    }

    [HttpGet("schedule")]
    public async Task<ActionResult<List<CalendarEventDto>>> Schedule([FromQuery] DateOnly? from, [FromQuery] DateOnly? to)
    {
        var userId = _identity.GetUserId();
        var start = from ?? DateOnly.FromDateTime(DateTime.UtcNow);
        var end = to ?? start.AddMonths(1);
        var schedules = await _context.OutfitSchedules
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
        var outfit = await _context.Outfits.AnyAsync(o => o.Id == request.OutfitId && o.UserId == userId);
        if (!outfit) return NotFound("Outfit not found.");

        var date = request.Date ?? DateOnly.FromDateTime(DateTime.UtcNow);
        _context.OutfitWearLogs.Add(new OutfitWearLog
        {
            UserId = userId,
            OutfitId = request.OutfitId,
            Date = date,
            AcceptedSuggestion = request.AcceptedSuggestion
        });
        await _context.SaveChangesAsync();
        return Ok();
    }

    private static OutfitDto ToDto(Outfit outfit) =>
        new(outfit.Id, outfit.Name, outfit.CreatedAt,
            outfit.Items.Select(i => new OutfitItemDto(i.ClothingItemId, i.ClothingItem.Name, i.ClothingItem.Category, i.ClothingItem.ImageUrl)).ToList());
}
