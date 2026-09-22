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
<<<<<<< Updated upstream
    private readonly ApplicationDbContext _db;
    private readonly IUserIdentityService _identity;

    public OutfitController(ApplicationDbContext db, IUserIdentityService identity)
    {
        _db = db;
=======
    private readonly ApplicationDbContext _context;
    private readonly IUserIdentityService _identity;

    public OutfitController(ApplicationDbContext context, IUserIdentityService identity)
    {
        _context = context;
>>>>>>> Stashed changes
        _identity = identity;
    }

    [HttpGet]
    public async Task<ActionResult<List<OutfitDto>>> Get()
    {
        var userId = _identity.GetUserId();
<<<<<<< Updated upstream
        var outfits = await _db.Outfits
=======
        var outfits = await _context.Outfits
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
        var owned = await _db.ClothingItems
=======
        var owned = await _context.ClothingItems
>>>>>>> Stashed changes
            .Where(c => c.UserId == userId && !c.IsWishlist && ids.Contains(c.Id))
            .ToListAsync();

        if (owned.Count != ids.Count)
            return BadRequest("One or more clothing items are not owned by the current user.");

        var outfit = new Outfit { UserId = userId, Name = request.Name.Trim() };
        foreach (var item in owned)
            outfit.Items.Add(new OutfitItem { ClothingItemId = item.Id });

<<<<<<< Updated upstream
        _db.Outfits.Add(outfit);
        await _db.SaveChangesAsync();

        await _db.Entry(outfit).Collection(x => x.Items).Query().Include(x => x.ClothingItem).LoadAsync();
=======
        _context.Outfits.Add(outfit);
        await _context.SaveChangesAsync();

        await _context.Entry(outfit).Collection(x => x.Items).Query().Include(x => x.ClothingItem).LoadAsync();
>>>>>>> Stashed changes
        return Ok(ToDto(outfit));
    }

    [HttpPost("schedule")]
    public async Task<IActionResult> Schedule(ScheduleOutfitRequest request)
    {
        var userId = _identity.GetUserId();
<<<<<<< Updated upstream
        var outfit = await _db.Outfits.FirstOrDefaultAsync(o => o.Id == request.OutfitId && o.UserId == userId);
        if (outfit == null) return NotFound("Outfit not found.");

        var existing = await _db.OutfitSchedules.FirstOrDefaultAsync(x => x.UserId == userId && x.Date == request.Date);
        if (existing == null)
            _db.OutfitSchedules.Add(new OutfitSchedule { UserId = userId, OutfitId = request.OutfitId, Date = request.Date });
        else
            existing.OutfitId = request.OutfitId;

        await _db.SaveChangesAsync();
=======
        var outfit = await _context.Outfits.FirstOrDefaultAsync(o => o.Id == request.OutfitId && o.UserId == userId);
        if (outfit == null) return NotFound("Outfit not found.");

        var existing = await _context.OutfitSchedules.FirstOrDefaultAsync(x => x.UserId == userId && x.Date == request.Date);
        if (existing == null)
            _context.OutfitSchedules.Add(new OutfitSchedule { UserId = userId, OutfitId = request.OutfitId, Date = request.Date });
        else
            existing.OutfitId = request.OutfitId;

        await _context.SaveChangesAsync();
>>>>>>> Stashed changes
        return Ok();
    }

    [HttpGet("schedule")]
    public async Task<ActionResult<List<CalendarEventDto>>> Schedule([FromQuery] DateOnly? from, [FromQuery] DateOnly? to)
    {
        var userId = _identity.GetUserId();
        var start = from ?? DateOnly.FromDateTime(DateTime.UtcNow);
        var end = to ?? start.AddMonths(1);
<<<<<<< Updated upstream
        var schedules = await _db.OutfitSchedules
=======
        var schedules = await _context.OutfitSchedules
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
        var outfit = await _db.Outfits.AnyAsync(o => o.Id == request.OutfitId && o.UserId == userId);
        if (!outfit) return NotFound("Outfit not found.");

        var date = request.Date ?? DateOnly.FromDateTime(DateTime.UtcNow);
        _db.OutfitWearLogs.Add(new OutfitWearLog
=======
        var outfit = await _context.Outfits.AnyAsync(o => o.Id == request.OutfitId && o.UserId == userId);
        if (!outfit) return NotFound("Outfit not found.");

        var date = request.Date ?? DateOnly.FromDateTime(DateTime.UtcNow);
        _context.OutfitWearLogs.Add(new OutfitWearLog
>>>>>>> Stashed changes
        {
            UserId = userId,
            OutfitId = request.OutfitId,
            Date = date,
            AcceptedSuggestion = request.AcceptedSuggestion
        });
<<<<<<< Updated upstream
        await _db.SaveChangesAsync();
=======
        await _context.SaveChangesAsync();
>>>>>>> Stashed changes
        return Ok();
    }

    private static OutfitDto ToDto(Outfit outfit) =>
        new(outfit.Id, outfit.Name, outfit.CreatedAt,
            outfit.Items.Select(i => new OutfitItemDto(i.ClothingItemId, i.ClothingItem.Name, i.ClothingItem.Category, i.ClothingItem.ImageUrl)).ToList());
}
