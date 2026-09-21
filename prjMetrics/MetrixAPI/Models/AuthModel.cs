using System.ComponentModel.DataAnnotations;

namespace MetrixAPI.Models
{
    //authModel has classes related to firebase, login and sign up
    //AuthModel class all the data transfer object (classes) used for authentication
    
    //sign up needs email and password
        public class SignUpRequest
        {
            [Required]
            public string Email { get; set; } = string.Empty;

            [Required]
            public string Password { get; set; } = string.Empty;
        }

    //sign in objects email and password
        public class SignInRequest
        {
            [Required]
            public string Email { get; set; } = string.Empty;

            [Required]
            public string Password { get; set; } = string.Empty;
        }

    //authresponse is returned by the API and sign in is successfull
        public class AuthResponse
        {
            public string Uid { get; set; } = string.Empty;
            public string Email { get; set; } = string.Empty;
            public string IdToken { get; set; } = string.Empty;
            public string RefreshToken { get; set; } = string.Empty;
        }

    // points to the firebase project linking to the json credential file
        public class FirebaseSettings
        {
            public string ProjectId { get; set; } = string.Empty;
            public string ApiKey { get; set; } = string.Empty;
            public string ServiceAccountPath { get; set; } = "google-services.json";
        }

    //for SSO returns the token taken to firebase
        public class GoogleSignInRequest
        {
            [Required]
            public string IdToken { get; set; } = string.Empty;
        }

        //public class GoogleSignUpRequest
        //{
        //    [Required]
        //    pubic string IdToken { get; set; } = string.Empty;
        //}
    
}

/*References
 
Watmore, J., 2018. Aspnet core registration login api. (Version 10.0) [Source Code]. Available at: < https://github.com/cornflourblue/aspnet-core-registration-login-api/blob/master/Dtos/UserDto.cs > [Accessed 17 September 2026]
 
 */