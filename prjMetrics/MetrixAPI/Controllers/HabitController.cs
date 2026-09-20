using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MetrixAPI.Models;
using MetrixAPI.Data;

namespace MetrixAPI.Controllers;

[Route("api/[controller]")]
[ApiController]
public class HabitsController : ControllerBase
{
    private readonly ApplicationDbContext _context;

    public HabitsController(ApplicationDbContext context)
    {
        _context = context;
    }

    // GET: api/Habits?userId=abc&date=2026-09-20
    [HttpGet]
    public async Task<ActionResult<IEnumerable<HabitItemDTO>>> GetHabits(
        [FromQuery] string userId, [FromQuery] DateOnly? date)
    {
        if (string.IsNullOrWhiteSpace(userId))
        {
            return BadRequest("userId is required.");
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
        var day = date ?? DateOnly.FromDateTime(DateTime.UtcNow);

        var habit = await _context.Habits
            .Include(h => h.Logs.Where(l => l.LogDate == day))
            .FirstOrDefaultAsync(h => h.Id == id);

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
        if (id != habitItemDTO.Id)
        {
            return BadRequest();
        }

        var habit = await _context.Habits.FindAsync(id);
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
        catch (DbUpdateConcurrencyException) when (!HabitExists(id))
        {
            return NotFound();
        }

        return NoContent();
    }

    // PUT: api/Habits/5/progress?progress=0.5&date=2026-09-20
    [HttpPut("{id}/progress")]
    public async Task<IActionResult> PutProgress(int id, [FromQuery] double progress, [FromQuery] DateOnly? date)
    {
        if (!HabitExists(id))
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
        if (string.IsNullOrWhiteSpace(habitItemDTO.UserId) || string.IsNullOrWhiteSpace(habitItemDTO.Label))
        {
            return BadRequest("userId and label are required.");
        }

        var habit = new Habit
        {
            UserId = habitItemDTO.UserId,
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
        var habit = await _context.Habits.FindAsync(id);
        if (habit == null)
        {
            return NotFound();
        }

        _context.Habits.Remove(habit);
        await _context.SaveChangesAsync();

        return NoContent();
    }

    private bool HabitExists(int id)
    {
        return _context.Habits.Any(e => e.Id == id);
    }

    // Expects Logs to be pre-filtered to a single day 
    private static HabitItemDTO ItemToDTO(Habit habit) =>
        new HabitItemDTO
        {
            Id = habit.Id,
            UserId = habit.UserId,
            Label = habit.Label,
            IconKey = habit.IconKey,
            ColorHex = habit.ColorHex,
            GoldCoins = habit.GoldCoins,
            Progress = habit.Logs.FirstOrDefault()?.Progress ?? 0
        };
}