namespace MetrixAPI.Models;

public class ProfileItemDTO
{
    public string DisplayName { get; set; } = string.Empty;
    public string Personality { get; set; } = string.Empty;
    public DateOnly? Birthday { get; set; }
    public string BodyKey { get; set; } = "base_body_f";
    public string HairKey { get; set; } = "hair_1";
    public string EyesKey { get; set; } = "eye1c";
    public string LipsKey { get; set; } = "lipstick6_grin";
}