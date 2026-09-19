using MetrixAPI.Models;
using MetrixAPI.Helpers;
using Firebase.Auth;
using Firebase.Auth.Providers;

using ClientAuthException = Firebase.Auth.FirebaseAuthException;

namespace MetrixAPI.Services
{
    public interface IAuthService
    {
        Task<AuthResponse> SignUpAsync(SignUpRequest request);
        Task<AuthResponse?> SignInAsync(SignInRequest request);
        Task<AuthResponse> SignInWithGoogleAsync(GoogleSignInRequest request);
    }

    public class AuthService(FirebaseAuthClient firebaseAuth) : IAuthService
    {
        public async Task<AuthResponse> SignUpAsync(SignUpRequest request)
        {
            try
            {
                var credentials = await firebaseAuth.CreateUserWithEmailAndPasswordAsync(request.Email.Trim(), request.Password);

                var signInResponse = await SignInAsync(new SignInRequest
                {
                    Email = request.Email,
                    Password = request.Password
                });

                return signInResponse
                    ?? throw new AppException("Account created, but automatic sign-in failed. Please sign in manually.");
            }
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

        public async Task<AuthResponse?> SignInAsync(SignInRequest request)
        {
            try
            {
                var credentials = await firebaseAuth.SignInWithEmailAndPasswordAsync(request.Email.Trim(), request.Password);

                var idToken = await credentials.User.GetIdTokenAsync();

                return new AuthResponse
                {
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

        public async Task<AuthResponse> SignInWithGoogleAsync(GoogleSignInRequest request)
        {
            try
            {
                var googleCredential = GoogleProvider.GetCredential(request.IdToken, OAuthCredentialTokenType.IdToken);
                var credentials = await firebaseAuth.SignInWithCredentialAsync(googleCredential);

                var idToken = await credentials.User.GetIdTokenAsync();

                return new AuthResponse
                {
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