using MetrixAPI.Data;
using MetrixAPI.Models;
using MetrixAPI.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace MetrixAPI.Controllers;

[ApiController]
[Route("api/self-care")]
[Authorize]
public class SelfCareController : ControllerBase
{
    private readonly ApplicationDbContext _db;
    private readonly IUserIdentityService _identity;
    public SelfCareController(ApplicationDbContext db, IUserIdentityService identity) { _db = db; _identity = identity; }

    [HttpPost("quiz")]
    public async Task<IActionResult> Save(SelfCareQuizRequest request)
    {
        var userId = _identity.GetUserId();
        _db.SelfCareQuizResults.Add(new SelfCareQuizResult { UserId = userId, Score = Math.Clamp(request.Score, 0, 5), Reflection = request.Reflection.Trim() });
        await _db.SaveChangesAsync();
        return Ok();
    }



    [HttpGet("latest")]
    public async Task<ActionResult<SelfCareQuizResult>> Latest()
    {
        var userId = _identity.GetUserId();
        var result = await _db.SelfCareQuizResults.Where(x => x.UserId == userId).OrderByDescending(x => x.CompletedAt).FirstOrDefaultAsync();
        return result == null ? NotFound() : Ok(result);
    }
}
