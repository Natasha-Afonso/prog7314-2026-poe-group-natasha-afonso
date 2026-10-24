using System.ComponentModel.DataAnnotations;

namespace MetrixAPI.Models;

//One completed self care check-in
//Reflection is the JSON list of the questions and answers, which the app reads back for the magazine
public class SelfCareQuizResult
{
    [Key] public int Id { get; set; }
    [Required] public string UserId { get; set; } = string.Empty;
    public int Score { get; set; }
    [Required] public string Reflection { get; set; } = string.Empty;
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
}
