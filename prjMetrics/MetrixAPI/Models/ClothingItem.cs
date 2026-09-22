using System.ComponentModel.DataAnnotations;

namespace MetrixAPI.Models
{
    public class ClothingItem
    {
        [Key]
        public int Id { get; set; }

        [Required]
        public string UserId { get; set; } = string.Empty;

        [Required]
        public string Name { get; set; } = string.Empty;

        [Required]
        public string Category { get; set; } = string.Empty;

        public string? ImageUrl { get; set; } = string.Empty;

        public bool IsWishlist { get; set; } = false;

        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    }
}