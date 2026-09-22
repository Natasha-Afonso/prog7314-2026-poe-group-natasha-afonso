using MetrixAPI.Models;
using Microsoft.EntityFrameworkCore;

namespace MetrixAPI.Data
{
    public class ApplicationDbContext : DbContext
    {
        public ApplicationDbContext(DbContextOptions<ApplicationDbContext> options): base(options) {}

      //Settings
        public DbSet<UserSettings> UserSettings { get; set; }
      
        public DbSet<BugReport> BugReports { get; set; }

        public DbSet<AppRating> AppRatings { get; set; }
        public DbSet<Journal> Journal { get; set; }


        // public DbSet<CalendarEvent> CalendarEvents { get; set; }
        // public DbSet<Outfit> Outfits { get; set; }
        
        //Wardrobe
       public DbSet<ClothingItem> ClothingItems { get; set; }
<<<<<<< Updated upstream
=======
       public DbSet<Outfit> Outfits { get; set; }
       public DbSet<OutfitItem> OutfitItems { get; set; }
       public DbSet<OutfitSchedule> OutfitSchedules { get; set; }

       
       public DbSet<OutfitWearLog> OutfitWearLogs { get; set; }

       //Magazine
       public DbSet<SelfCareQuizResult> SelfCareQuizResults { get; set; }
       
      // public DbSet<CalendarEvent> CalendarEvents { get; set; }
>>>>>>> Stashed changes
        // public DbSet<WishlistItem> WishlistItems { get; set; }
        // public DbSet<WeeklyMagazine> WeeklyMagazines { get; set; }


        //Habits
          public DbSet<Habit> Habits { get; set; }
        public DbSet<HabitLog> HabitLogs { get; set; }
   
   //Profile
   public DbSet<Profile> Profiles { get; set; }
    }
}


