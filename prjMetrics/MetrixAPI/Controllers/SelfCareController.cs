using MetrixAPI.Data;
using MetrixAPI.Models;
using MetrixAPI.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace MetrixAPI.Controllers;

[ApiController]
[Route("api/selfcare")]
[Authorize]
public class SelfCareController : ControllerBase
{
    private readonly ApplicationDbContext _db;
    private readonly IUserIdentityService _identity;

    public SelfCareController(ApplicationDbContext db, IUserIdentityService identity)
    {
        _db = db;
        _identity = identity;
    }

    //POST api/selfcare/quiz
    [HttpPost("quiz")]
    public async Task<ActionResult<SelfCareQuizDto>> SaveQuiz(SelfCareQuizRequest request)
    {
        if (request.Score < 1 || request.Score > 5)
            return BadRequest("Score must be between 1 and 5.");
        if (string.IsNullOrWhiteSpace(request.Reflection) || request.Reflection.Length > 4000)
            return BadRequest("A reflection of up to 4000 characters is required.");

        var row = new SelfCareQuizResult
        {
            UserId = _identity.GetUserId(),
            Score = request.Score,
            Reflection = request.Reflection,
            CreatedAt = DateTime.UtcNow
        };
        _db.SelfCareQuizResults.Add(row);
        await _db.SaveChangesAsync();

        return Ok(ToDto(row));
    }

    //GET api/selfcare/quiz?from=2026-10-19&to=2026-10-26
    [HttpGet("quiz")]
    public async Task<ActionResult<List<SelfCareQuizDto>>> GetQuizzes([FromQuery] DateOnly? from, [FromQuery] DateOnly? to)
    {
        var userId = _identity.GetUserId();
        var start = (from ?? DateOnly.FromDateTime(DateTime.UtcNow).AddDays(-30)).ToDateTime(TimeOnly.MinValue);
        var end = (to ?? DateOnly.FromDateTime(DateTime.UtcNow).AddDays(1)).ToDateTime(TimeOnly.MaxValue);

        var rows = await _db.SelfCareQuizResults
            .Where(x => x.UserId == userId && x.CreatedAt >= start && x.CreatedAt <= end)
            .OrderBy(x => x.CreatedAt)
            .ToListAsync();

        return rows.Select(ToDto).ToList();
    }

    private static SelfCareQuizDto ToDto(SelfCareQuizResult r) =>
        new(r.Id, r.CreatedAt, r.Score, r.Reflection);
}
