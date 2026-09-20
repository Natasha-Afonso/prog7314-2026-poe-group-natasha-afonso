using System.ComponentModel.DataAnnotations;

namespace MetrixAPI.Models
{
    public class UserSettings
    {
        [Key]
        public int Id {get; set;}
        [Required]
        public string UserId {get; set;} = string.Empty;
        public string Language {get; set;} ="English";
        public string Theme {get; set;} ="System";
        public bool PushNotifications {get; set;} = true;
        public int FontSize {get; set;} = 16;
        public bool HighContrast {get; set;} = false;
        public DateTime UpdatedAt {get; set;} = DateTime.UtcNow;

    }
}