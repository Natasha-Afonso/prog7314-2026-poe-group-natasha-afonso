using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MetrixAPI.Models;
using MetrixAPI.Data;

namespace MetrixAPI.Controllers;

[Authorize]
[Route("api/[controller]")]
[ApiController]
public class HabitsController : ControllerBase
{
    private readonly ApplicationDbContext _context;

    public HabitsController(ApplicationDbContext context)
    {
        _context = context;
    }

    // Firebase user id from the token (the same claim AuthController.Me uses)
    private string? CurrentUserId => User.FindFirst("user_id")?.Value;

    // GET: api/Habits?date=2026-09-20
    [HttpGet]
    public async Task<ActionResult<IEnumerable<HabitItemDTO>>> GetHabits([FromQuery] DateOnly? date)
    {
        var userId = CurrentUserId;
        if (userId == null)
        {
            return Unauthorized();
        }

        var day = date ?? DateOnly.FromDateTime(DateTime.UtcNow);

        var habits = await _context.Habits
            .Where(h => h.UserId == userId)
            .Include(h => h.Logs.Where(l => l.LogDate == day))
            .OrderBy(h => h.Id)
            .ToListAsync();

        return habits.Select(h => ItemToDTO(h)).ToList();
    }

    // GET: api/Habits/5?date=2026-09-20
    [HttpGet("{id}")]
    public async Task<ActionResult<HabitItemDTO>> GetHabit(int id, [FromQuery] DateOnly? date)
    {
        var userId = CurrentUserId;
        if (userId == null)
        {
            return Unauthorized();
        }

        var day = date ?? DateOnly.FromDateTime(DateTime.UtcNow);

        var habit = await _context.Habits
            .Include(h => h.Logs.Where(l => l.LogDate == day))
            .FirstOrDefaultAsync(h => h.Id == id && h.UserId == userId);

        if (habit == null)
        {
            return NotFound();
        }

        return ItemToDTO(habit);
    }

    // PUT: api/Habits/5   (edits label, icon, colour, coins)
    [HttpPut("{id}")]
    public async Task<IActionResult> PutHabit(int id, HabitItemDTO habitItemDTO)
    {
        var userId = CurrentUserId;
        if (userId == null)
        {
            return Unauthorized();
        }

        if (id != habitItemDTO.Id)
        {
            return BadRequest();
        }

        var habit = await _context.Habits.FirstOrDefaultAsync(h => h.Id == id && h.UserId == userId);
        if (habit == null)
        {
            return NotFound();
        }

        habit.Label = habitItemDTO.Label;
        habit.IconKey = habitItemDTO.IconKey;
        habit.ColorHex = habitItemDTO.ColorHex;
        habit.GoldCoins = habitItemDTO.GoldCoins;

        try
        {
            await _context.SaveChangesAsync();
        }
        catch (DbUpdateConcurrencyException) when (!HabitExists(id, userId))
        {
            return NotFound();
        }

        return NoContent();
    }

    // PUT: api/Habits/5/progress?progress=0.5&date=2026-09-20
    [HttpPut("{id}/progress")]
    public async Task<IActionResult> PutProgress(int id, [FromQuery] double progress, [FromQuery] DateOnly? date)
    {
        var userId = CurrentUserId;
        if (userId == null)
        {
            return Unauthorized();
        }

        if (!HabitExists(id, userId))
        {
            return NotFound();
        }

        var day = date ?? DateOnly.FromDateTime(DateTime.UtcNow);

        var log = await _context.HabitLogs
            .FirstOrDefaultAsync(l => l.HabitId == id && l.LogDate == day);

        if (log == null)
        {
            log = new HabitLog { HabitId = id, LogDate = day };
            _context.HabitLogs.Add(log);
        }

        log.Progress = Math.Clamp(progress, 0, 1);
        await _context.SaveChangesAsync();

        return NoContent();
    }

    // POST: api/Habits
    [HttpPost]
    public async Task<ActionResult<HabitItemDTO>> PostHabit(HabitItemDTO habitItemDTO)
    {
        var userId = CurrentUserId;
        if (userId == null)
        {
            return Unauthorized();
        }

        if (string.IsNullOrWhiteSpace(habitItemDTO.Label))
        {
            return BadRequest("label is required.");
        }

        var habit = new Habit
        {
            UserId = userId,   // from the token, never from the client
            Label = habitItemDTO.Label.Trim(),
            IconKey = habitItemDTO.IconKey,
            ColorHex = habitItemDTO.ColorHex,
            GoldCoins = habitItemDTO.GoldCoins,
            CreatedAt = DateTime.UtcNow
        };

        _context.Habits.Add(habit);
        await _context.SaveChangesAsync();

        return CreatedAtAction(
            nameof(GetHabit),
            new { id = habit.Id },
            ItemToDTO(habit));
    }

    // DELETE: api/Habits/5
    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteHabit(int id)
    {
        var userId = CurrentUserId;
        if (userId == null)
        {
            return Unauthorized();
        }

        var habit = await _context.Habits.FirstOrDefaultAsync(h => h.Id == id && h.UserId == userId);
        if (habit == null)
        {
            return NotFound();
        }

        _context.Habits.Remove(habit);
        await _context.SaveChangesAsync();

        return NoContent();
    }

    private bool HabitExists(int id, string userId)
    {
        return _context.Habits.Any(e => e.Id == id && e.UserId == userId);
    }

    // Expects Logs to be pre-filtered to a single day 
    private static HabitItemDTO ItemToDTO(Habit habit) =>
        new HabitItemDTO
        {
            Id = habit.Id,
            Label = habit.Label,
            IconKey = habit.IconKey,
            ColorHex = habit.ColorHex,
            GoldCoins = habit.GoldCoins,
            Progress = habit.Logs.FirstOrDefault()?.Progress ?? 0
        };
}