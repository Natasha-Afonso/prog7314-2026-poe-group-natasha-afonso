using MetrixAPI.Models;
using MetrixAPI.Helpers;
using Firebase.Auth;
using Firebase.Auth.Providers;

//declaring the firebase authentication client 
using ClientAuthException = Firebase.Auth.FirebaseAuthException;

//(Watmore, 2018)
namespace MetrixAPI.Services
{
    //defining the interface for dependency injection
    public interface IAuthService
    {
        Task<AuthResponse> SignUpAsync(SignUpRequest request); //for users to register
        Task<AuthResponse?> SignInAsync(SignInRequest request); //users to login
        Task<AuthResponse> SignInWithGoogleAsync(GoogleSignInRequest request);//for google sso
    }

    public class AuthService(FirebaseAuthClient firebaseAuth) : IAuthService
    {
        //method to sign up a new user
        public async Task<AuthResponse> SignUpAsync(SignUpRequest request)
        {
            try
            {
                //gets firebase to get the email and password
                var credentials = await firebaseAuth.CreateUserWithEmailAndPasswordAsync(request.Email.Trim(), request.Password);

                //signin works after user puts in their email and password
                var signInResponse = await SignInAsync(new SignInRequest
                {
                    Email = request.Email,
                    Password = request.Password
                });

                return signInResponse
                    ?? throw new AppException("Account created, but automatic sign-in failed. Please sign in manually.");
            }
            //catch methods for error handling
            catch (ClientAuthException ex) when (ex.Reason == AuthErrorReason.EmailExists)
            {
                throw new AppException("That email address already exists");
            }
            catch (ClientAuthException ex) when (ex.Reason == AuthErrorReason.WeakPassword)
            {
                throw new AppException("Password characters should be 6 characters minimum");
            }
            catch (ClientAuthException)
            {
                throw new AppException("Error creating account. Please try again");
            }
        }

        //method for the login to authenticate the user through firebase
        public async Task<AuthResponse?> SignInAsync(SignInRequest request)
        {
            try
            {
                //authenticates the user credentials against the firebase accounts
                var credentials = await firebaseAuth.SignInWithEmailAndPasswordAsync(request.Email.Trim(), request.Password);
                var idToken = await credentials.User.GetIdTokenAsync(); //created JWT for logged in user

                return new AuthResponse
                { //authResponse objects has the uid, email and JWT
                    Uid = credentials.User.Uid,
                    Email = credentials.User.Info.Email ?? request.Email,
                    IdToken = idToken,
                    RefreshToken = credentials.User.Credential.RefreshToken
                };
            }
            catch (ClientAuthException)
            {
                return null;
            }
        }

        //method for google sign 
        public async Task<AuthResponse> SignInWithGoogleAsync(GoogleSignInRequest request)
        {
            try
            {
                //firebase authenticates by getting Google ID tokens linking it to the firebase credential
                var googleCredential = GoogleProvider.GetCredential(request.IdToken, OAuthCredentialTokenType.IdToken);
                var credentials = await firebaseAuth.SignInWithCredentialAsync(googleCredential);

                var idToken = await credentials.User.GetIdTokenAsync();

                return new AuthResponse
                { //returns successfuly object details
                    Uid = credentials.User.Uid,
                    Email = credentials.User.Info.Email ?? string.Empty,
                    IdToken = idToken,
                    RefreshToken = credentials.User.Credential.RefreshToken
                };
            }
            catch (ClientAuthException)
            {
                throw new AppException("Could not sign in with Google. Please try again.");
            }
        }
    }
}

/*References
 
Watmore, J., 2018. Aspnet core registration login api. (Version 10.0) [Source Code]. Available at: < https://github.com/cornflourblue/aspnet-core-registration-login-api/blob/master/Services/UserService.cs > [Accessed 17 September 2026]
 
 */