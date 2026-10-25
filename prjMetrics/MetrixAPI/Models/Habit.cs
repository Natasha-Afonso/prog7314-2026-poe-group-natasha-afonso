namespace MetrixAPI.Models
{
    public class Habit
    {
        public int Id { get; set;}

        public string UserId { get; set; } = string.Empty;
        public string Label { get; set; } = string.Empty;
        public string IconKey { get; set;} = "default";
        public string ColorHex{ get; set;} = "#29B6F6";
    
        public int GoldCoins { get; set; } = 5;
        public int TargetCount { get; set; } = 1;   // times per day the habit is done

        public DateTime CreatedAt { get; set; }

        public List<HabitLog> Logs  { get; set;} = new();
    }
}