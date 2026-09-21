using System.ComponentModel.DataAnnotations;

namespace MetrixAPI.Models
{
    public class BugReport
    {
        [Key]
        public int Id {get; set;}
        [Required]
        public string UserId {get; set;} = string.Empty;
        [Required]
        [MaxLength(200)]
        public string Title {get; set;}= string.Empty;
        [Required]
        [MaxLength(4000)]
        public string Description {get; set;} = string.Empty;
        public DateTime CreatedAt {get; set;} = DateTime.UtcNow;
    }
}
