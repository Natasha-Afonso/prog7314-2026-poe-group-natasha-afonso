namespace MetrixAPI.Models
{
    public class HabitItemDTO
    {
        public int Id { get; set;}
        public string Label { get; set; } = string.Empty;
        public string IconKey { get; set;} = "default";
        public string ColorHex{ get; set;} = "#29B6F6";
        public int GoldCoins { get; set; } = 5; 
       public double Progress { get; set;}

    }
}