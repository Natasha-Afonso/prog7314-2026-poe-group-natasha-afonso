using System.ComponentModel.DataAnnotations;

namespace MetrixAPI.Models
{
    public class Journal
    {
        [Key]
        public int JournalId { get; set; }

        [Required]
        public string UserId { get; set; } = string.Empty; //map to correct user on firebase

        [Required]
        public string Mood { get; set; } = string.Empty;

        [Required]
        public string Text { get; set; } = string.Empty;


        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
        public DateTime? UpdatedAt { get; set; } //updates the time when user updates a diary entry


    }
}