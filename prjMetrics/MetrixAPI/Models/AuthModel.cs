using System.ComponentModel.DataAnnotations;

namespace MetrixAPI.Models
{
    //authModel has classes related to firebase, login and sign up
    public class AuthModel
    {
        public class SignUpRequest
        {
            [Required]
            public string Email { get; set; } = string.Empty;

            [Required]
            public string Password { get; set; } = string.Empty;
        }

        public class SignInRequest
        {
            [Required]
            public string Email { get; set; } = string.Empty;

            [Required]
            public string Password { get; set; } = string.Empty;
        }

        public class AuthResponse
        {
            public string Uid { get; set; } = string.Empty;
            public string Email { get; set; } = string.Empty;
            public string IdToken { get; set; } = string.Empty;
            public string RefreshToken { get; set; } = string.Empty;
        }

        public class FirebaseSettings
        {
            public string ProjectId { get; set; } = string.Empty;
            public string ApiKey { get; set; } = string.Empty;
            public string ServiceAccountPath { get; set; } = "firebase.json";
        }

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
}