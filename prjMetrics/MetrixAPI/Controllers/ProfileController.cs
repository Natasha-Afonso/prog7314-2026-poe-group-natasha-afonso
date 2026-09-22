using System.Text.RegularExpressions;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using MetrixAPI.Models;
using MetrixAPI.Data;

namespace MetrixAPI.Controllers;

// [Authorize]: only signed-in users (a valid Firebase token) can call these endpoints.
[Authorize]
[Route("api/[controller]")]   // this controller answers at api/Profile
[ApiController]
public class ProfileController : ControllerBase
{
    private readonly ApplicationDbContext _context;   // gives access to the database tables

    // Exactly "#" followed by 6 hex digits. Stops junk being saved as an eye colour.
    private static readonly Regex HexColor = new(@"^#[0-9A-Fa-f]{6}$");

    public ProfileController(ApplicationDbContext context)
    {
        _context = context;
    }

    // The signed-in user's Firebase id, read from the token .
    // Because it comes from the token, a user can only ever read or write their own profile.
    private string? CurrentUserId => User.FindFirst("user_id")?.Value;

    // GET: api/Profile
    // 404 means "this user has no saved profile yet" (the app then starts from defaults).
    [HttpGet]
    public async Task<ActionResult<ProfileItemDTO>> GetProfile()
    {
        var userId = CurrentUserId;
        if (userId == null)
        {
            return Unauthorized();
        }

        // FindAsync looks the row up by its primary key (UserId)
        var profile = await _context.Profiles.FindAsync(userId);
        if (profile == null)
        {
            return NotFound();
        }

        return ItemToDTO(profile);
    }

    // PUT: api/Profile
    // Creates the profile the first time and updates it every time after that.
    [HttpPut]
    public async Task<ActionResult<ProfileItemDTO>> PutProfile(ProfileItemDTO profileItemDTO)
    {
        var userId = CurrentUserId;
        if (userId == null)
        {
            return Unauthorized();
        }

        // reject bad input before touching the database
        var error = Validate(profileItemDTO);
        if (error != null)
        {
            return BadRequest(error);
        }

        var profile = await _context.Profiles.FindAsync(userId);
        if (profile == null)
        {
            // first save for this user: create the row
            profile = new Profile { UserId = userId, CreatedAt = DateTime.UtcNow };
            _context.Profiles.Add(profile);
        }

        // copy every field from the request onto the row
        profile.UserName = profileItemDTO.UserName.Trim();
        profile.Personality = profileItemDTO.Personality;
        profile.Birthday = profileItemDTO.Birthday;
        profile.Aspiration = profileItemDTO.Aspiration;
        profile.BodyRef = profileItemDTO.BodyRef;
        profile.HairRef = profileItemDTO.HairRef;
        profile.ExtensionRef = profileItemDTO.ExtensionRef;
        profile.EyeColor = profileItemDTO.EyeColor;
        profile.MouthRef = profileItemDTO.MouthRef;
        profile.LipstickRef = profileItemDTO.LipstickRef;

        await _context.SaveChangesAsync();

        return ItemToDTO(profile);
    }

    // Returns an error message, or null when everything is fine.
    private static string? Validate(ProfileItemDTO dto)
    {
        if (string.IsNullOrWhiteSpace(dto.UserName) || dto.UserName.Trim().Length > 50)
        {
            return "displayName is required (max 50 characters).";
        }

        if (!HexColor.IsMatch(dto.EyeColor))
        {
            return "eyeColor must look like #RRGGBB.";
        }

        // every text column is NVARCHAR(50), so anything longer would fail at the database
        string[] shortTexts =
        {
            dto.Personality, dto.Aspiration, dto.BodyRef,
            dto.HairRef, dto.ExtensionRef, dto.MouthRef, dto.LipstickRef
        };
        if (shortTexts.Any(text => text.Length > 50))
        {
            return "personality, aspiration and the avatar keys can be at most 50 characters.";
        }

        return null;
    }

    // Row -> DTO (the shape the app receives)
    private static ProfileItemDTO ItemToDTO(Profile profile) =>
        new ProfileItemDTO
        {
            UserName = profile.UserName,
            Personality = profile.Personality,
            Birthday = profile.Birthday,
            Aspiration = profile.Aspiration,
            BodyRef = profile.BodyRef,
            HairRef = profile.HairRef,
            ExtensionRef = profile.ExtensionRef,
            EyeColor = profile.EyeColor,
            MouthRef = profile.MouthRef,
            LipstickRef = profile.LipstickRef
        };
}

/* References
Microsoft Learn, 2026. Tutorial: Create a controller-based web API with ASP.NET Core. [online] Available at: < https://learn.microsoft.com/en-us/aspnet/core/tutorials/first-web-api?view=aspnetcore-10.0 > [Accessed 21 September 2026]
*/