using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using MetrixAPI.Models;
using MetrixAPI.Data;

namespace MetrixAPI.Controllers;

[Authorize]
[Route("api/[controller]")]
[ApiController]
public class ProfileController : ControllerBase
{
    private readonly ApplicationDbContext _context;

    public ProfileController(ApplicationDbContext context)
    {
        _context = context;
    }

    private string? CurrentUserId => User.FindFirst("user_id")?.Value;

    // GET: api/Profile   (404 = this user hasn't done onboarding yet)
    [HttpGet]
    public async Task<ActionResult<ProfileItemDTO>> GetProfile()
    {
        var userId = CurrentUserId;
        if (userId == null)
        {
            return Unauthorized();
        }

        var profile = await _context.Profiles.FindAsync(userId);
        if (profile == null)
        {
            return NotFound();
        }

        return ItemToDTO(profile);
    }

    // PUT: api/Profile   (creates the profile the first time, updates it after that)
    [HttpPut]
    public async Task<ActionResult<ProfileItemDTO>> PutProfile(ProfileItemDTO profileDTO)
    {
        var userId = CurrentUserId;
        if (userId == null)
        {
            return Unauthorized();
        }

        if (string.IsNullOrWhiteSpace(profileDTO.DisplayName) || profileDTO.DisplayName.Trim().Length > 50)
        {
            return BadRequest("displayName is required (max 50 characters).");
        }

        var profile = await _context.Profiles.FindAsync(userId);
        if (profile == null)
        {
            profile = new Profile { UserId = userId, CreatedAt = DateTime.UtcNow };
            _context.Profiles.Add(profile);
        }

        profile.DisplayName = profileDTO.DisplayName.Trim();
        profile.Personality = profileDTO.Personality;
        profile.Birthday = profileDTO.Birthday;
        profile.BodyKey = profileDTO.BodyKey;
        profile.HairKey = profileDTO.HairKey;
        profile.EyesKey = profileDTO.EyesKey;
        profile.LipsKey = profileDTO.LipsKey;

        await _context.SaveChangesAsync();

        return ItemToDTO(profile);
    }

    private static ProfileItemDTO ItemToDTO(Profile profile) =>
        new ProfileItemDTO
        {
            DisplayName = profile.DisplayName,
            Personality = profile.Personality,
            Birthday = profile.Birthday,
            BodyKey = profile.BodyKey,
            HairKey = profile.HairKey,
            EyesKey = profile.EyesKey,
            LipsKey = profile.LipsKey
        };
}