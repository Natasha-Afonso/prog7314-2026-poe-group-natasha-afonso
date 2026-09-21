using System.ComponentModel.DataAnnotations;

namespace MetrixAPI.Models
{
    public class UserSettings
    {
        [Key]
        public int Id {get; set;}
        [Required]
        public string UserId {get; set;} = string.Empty;
        [Required]
        public string Language {get; set;} ="English";
        [Required]
        public string Theme {get; set;} ="System";
        public bool PushNotifications {get; set;} = true;
        [Required]
        public string FontSize {get; set;} = "Medium";
        public bool HighContrast {get; set;} = false;
        public DateTime UpdatedAt {get; set;} = DateTime.UtcNow;

    }
}