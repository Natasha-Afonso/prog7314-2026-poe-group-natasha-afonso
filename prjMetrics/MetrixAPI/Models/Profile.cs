using System.ComponentModel.DataAnnotations;

namespace MetrixAPI.Models;

public class Profile
{
    [Key]
    public string UserId { get; set; } = string.Empty;
    public string DisplayName { get; set; } = string.Empty;
    public string Personality { get; set; } = string.Empty;
    public DateOnly? Birthday { get; set; }
    public string BodyKey { get; set; } = "base_body_f";
    public string HairKey { get; set; } = "hair_1";
    public string EyesKey { get; set; } = "eye1c";
    public string LipsKey { get; set; } = "lipstick6_grin";
    public DateTime CreatedAt { get; set; }
}