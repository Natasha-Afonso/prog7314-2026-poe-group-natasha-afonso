using System.ComponentModel.DataAnnotations;

namespace MetrixAPI.Models;

public class OutfitWearLog
{
    [Key] public int Id { get; set; }
    [Required] public string UserId { get; set; } = string.Empty;
    public int OutfitId { get; set; }
    public DateOnly Date { get; set; }
    public bool AcceptedSuggestion { get; set; }
    public Outfit Outfit { get; set; } = null!;
}
