namespace MetrixAPI.Models
{
    public class HabitLog
    {
        public int Id { get; set;}
        public int HabitId { get; set; } 
        public DateOnly LogDate { get; set; } 
       public double Progress { get; set;}
    }
}