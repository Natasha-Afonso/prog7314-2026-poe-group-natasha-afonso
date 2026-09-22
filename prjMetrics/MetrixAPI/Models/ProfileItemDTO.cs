namespace MetrixAPI.Models;

// What travels over the API (the JSON the app sends and receives).
public class ProfileItemDTO
{
    public string UserName { get; set; } = string.Empty;
    public string Personality { get; set; } = string.Empty;
    public DateOnly? Birthday { get; set; }                       // JSON form: "2003-09-15"
    public string Aspiration { get; set; } = string.Empty;

    public string BodyRef { get; set; } = "base_body_f";
    public string HairRef { get; set; } = "hair_1";
    public string ExtensionRef { get; set; } = "extention_hair";
    public string EyeColor { get; set; } = "#240C0C";
    public string MouthRef { get; set; } = "grin_mouth";
    public string LipstickRef { get; set; } = "lipstick6_grin";
}