namespace MetrixAPI.Models;

//(Nimdinu, 2025)
public record CreateOutfitRequest(string Name, List<int> ClothingItemIds);
public record ScheduleOutfitRequest(int OutfitId, DateOnly Date);
public record WearOutfitRequest(int OutfitId, bool AcceptedSuggestion, DateOnly? Date);
public record SelfCareQuizRequest(int Score, string Reflection);

public record OutfitItemDto(int Id, string Name, string Category, string? ImageUrl);
public record OutfitDto(int Id, string Name, DateTime CreatedAt, List<OutfitItemDto> Items);
public record CalendarEventDto(DateOnly Date, string Title, string Type, int? OutfitId, string? OutfitName);
public record WearLogDto(DateOnly Date, int OutfitId, string OutfitName, bool AcceptedSuggestion, List<OutfitItemDto> Items);
public record SelfCareQuizDto(int Id, DateTime CreatedAt, int Score, string Reflection);
public record SelfCareQuizRequest(int Score, string Reflection);

public record MagazineDto(
    DateOnly WeekStart,
    DateOnly WeekEnd,
    int GoalsAchieved,
    int JournalEntries,
    int OutfitsWorn,
    int SuggestionsAccepted,
    int SuggestionsRejected,
    string? FavouriteOutfit,
    string? LatestMood,
    string? SelfCareReflection
);
/*Reference List:
Nimdinu, N. 2025. Understanding Data Transfer Objects (DTOs) in C# .NET: Best Practices & Examples. [source code] Available at:< https://medium.com/@20011002nimeth/understanding-data-transfer-objects-dtos-in-c-net-best-practices-examples-fe3e90238359> [Accessed 21 September 2026]
*/
