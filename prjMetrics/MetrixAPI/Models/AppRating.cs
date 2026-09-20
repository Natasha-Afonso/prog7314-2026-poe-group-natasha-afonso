using System.ComponentModel.DataAnnotations;

namespace MetrixAPI.Models
{
    public class AppRating
    {
        [Key]
        public int Id {get; set;}
        [Required]
        public string UserId {get; set;} = string.Empty;

        [Range(1, 5)]
        public int Rating {get; set;}
        [MaxLength(1000)]
        public string? Comment {get; set;}
        public DateTime CreatedAt {get; set;} = DateTime.UtcNow;
    }

}