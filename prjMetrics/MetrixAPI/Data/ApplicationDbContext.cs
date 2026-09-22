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
        // public DbSet<WishlistItem> WishlistItems { get; set; }
        // public DbSet<WeeklyMagazine> WeeklyMagazines { get; set; }


        //Habits
          public DbSet<Habit> Habits { get; set; }
        public DbSet<HabitLog> HabitLogs { get; set; }
   
   //Profile
   public DbSet<Profile> Profiles { get; set; }
    }
}


