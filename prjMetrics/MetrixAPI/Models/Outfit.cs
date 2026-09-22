using System.ComponentModel.DataAnnotations;

namespace MetrixAPI.Models;

public class Outfit
{
    [Key] public int Id { get; set; }
    [Required] public string UserId { get; set; } = string.Empty;
    [Required] public string Name { get; set; } = string.Empty;
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public List<OutfitItem> Items { get; set; } = new();
}

public class OutfitItem
{
    [Key] public int Id { get; set; }
    public int OutfitId { get; set; }
    public int ClothingItemId { get; set; }
    public Outfit Outfit { get; set; } = null!;
    public ClothingItem ClothingItem { get; set; } = null!;
}