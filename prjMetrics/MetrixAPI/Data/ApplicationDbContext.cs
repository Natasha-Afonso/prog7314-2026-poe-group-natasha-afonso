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
        public DbSet<Biometric> Biometric { get; set; }


        // public DbSet<CalendarEvent> CalendarEvents { get; set; }
        public DbSet<Outfit> Outfits { get; set; }
        
        //Wardrobe
        public DbSet<ClothingItem> ClothingItems { get; set; }
        
        // public DbSet<WishlistItem> WishlistItems { get; set; }
        // public DbSet<WeeklyMagazine> WeeklyMagazines { get; set; }
        
        public DbSet<OutfitItem> OutfitItems { get; set; }
        public DbSet<OutfitSchedule> OutfitSchedules { get; set; }
        public DbSet<OutfitWearLog> OutfitWearLogs { get; set; }

        //Habits
        public DbSet<Habit> Habits { get; set; }
        public DbSet<HabitLog> HabitLogs { get; set; }
   
        //Profile
        public DbSet<Profile> Profiles { get; set; }
        
        //(StackOverflow, 2025)
        //setting unique constraint for user to have a specific biometric data
        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);
            modelBuilder.Entity<Biometric>().HasIndex(b => b.UserId).IsUnique();
        }
    }
}

/*References
 
StackOverflow, 2025. Setting unique constraint with fluent API?. (Version 10.0)[Source Code]. Available at: < https://stackoverflow.com/questions/21573550/setting-unique-constraint-with-fluent-api > [Accessed 24 October 2026]
 
*/


