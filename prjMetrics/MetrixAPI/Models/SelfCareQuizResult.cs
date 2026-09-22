using System.ComponentModel.DataAnnotations;

namespace MetrixAPI.Models;

public class SelfCareQuizResult
{
    [Key] public int Id { get; set; }
    [Required] public string UserId { get; set; } = string.Empty;
    public int Score { get; set; }
    public string Reflection { get; set; } = string.Empty;
    public DateTime CompletedAt { get; set; } = DateTime.UtcNow;
}
