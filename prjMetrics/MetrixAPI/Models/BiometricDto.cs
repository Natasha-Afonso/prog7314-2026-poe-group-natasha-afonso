using System.ComponentModel.DataAnnotations;

//(Watmore, 2018)
namespace MetrixAPI.Models
{
    //BiometricDto has class all the data transfer object (classes) used for setting up biometric authentication
    //the request (what the user enters) and the response what the JSON returns

    //biometrics fallback pin objects
    public class SavePinRequest
    {
        [Required]
        public string NewPin { get; set; } = string.Empty;
    }

    //biometric pin response is returned by the API and pin is added
    public class BiometricResponse
    {
        public bool Success { get; set; }
        public bool Locked { get; set; }
        public string? Message { get; set; } = string.Empty;
        public int RetryAfterSeconds { get; set; }
        public int AttemptsRemaining { get; set; }
    }

    public class VerifyPinRequest
    {
        [Required]
        public string Pin { get; set; } = string.Empty;
    }

    public class BiometricStatusResponse
    {
        public bool HasPin { get; set; }
        public bool BiometricEnabled { get; set; }
    }

    public class BiometricRequest
    {
        public bool Enabled { get; set; }
    }

}

/*References
 
Watmore, J., 2018. Aspnet core registration login api. (Version 10.0) [Source Code]. Available at: < https://github.com/cornflourblue/aspnet-core-registration-login-api/blob/master/Dtos/UserDto.cs > [Accessed 17 September 2026]
 
 */