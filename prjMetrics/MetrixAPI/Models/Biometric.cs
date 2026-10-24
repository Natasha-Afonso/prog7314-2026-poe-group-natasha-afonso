using System.ComponentModel.DataAnnotations;

namespace MetrixAPI.Models
{
    public class Biometric
    {
        [Key]
        public int Id { get; set; }

        [Required]
        public string UserId { get; set; } = string.Empty;
        
        public int FailedAttempts{ get; set; } //failed attempts of entering fallback pin after 5 failed attempts it locks for a minute
        
        public string? PinHash { get; set; }
        
        public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;

        public bool BiometricEnabled { get; set; } //on or off biometric authentication in settings

        public DateTime? LockedoutEndUtc { get; set; } //time to calculate how long user is locked out after failed attempts
    }
}