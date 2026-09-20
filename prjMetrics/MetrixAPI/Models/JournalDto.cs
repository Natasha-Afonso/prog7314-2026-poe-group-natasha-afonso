using System.ComponentModel.DataAnnotations;

namespace MetrixAPI.Models
{
    //JournalDto class all the data transfer object (classes) used for journal entries
    //the request (what the user enters) and the response what the JSON returns

    //journal objects
    public class JournalRequest
    {
        [Required]
        public string Mood { get; set; } = string.Empty;

        [Required]
        public string Text { get; set; } = string.Empty;
    }

    //journal response is returned by the API and diary is added
    public class JournalResponse
    {
        public int JournalId { get; set; }
        public string Mood { get; set; } = string.Empty;
        public string Text { get; set; } = string.Empty;
        public DateTime CreatedAt { get; set; }
        public DateTime? UpdatedAt { get; set; }
    }
    
}

/*References
 
Watmore, J., 2018. Aspnet core registration login api. (Version 10.0) [Source Code]. Available at: < https://github.com/cornflourblue/aspnet-core-registration-login-api/blob/master/Dtos/UserDto.cs > [Accessed 17 September 2026]
 
 */