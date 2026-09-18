using System;
using MetrixAPI.Model;
using MetrixAPI.Helpers;
using Firebase.Auth

using AdminAuth = FirebaseAdmin.Auth.FirebaseAuth;
using ClientAuthException = Firebase.Auth.FirebaseAuthException;

namespace MetrixAPI.Services
{
    public interface IAuthService
    {
        Task<AuthResponse> SignUpAsync(SignUpRequest request);
        Task<AuthResponse> SignInAsync(SignInRequest request);
    }

    public class AuthService(FirebaseAuthClient firebaseAuth) : IAuthService
    {

        public async Task<AuthResponse> SignUpAsync(SignUpRequest request)
        {
            try
            {
                var credentials = await firebaseAuth.CreateUsersWithEmailAndPasswordAsync(request.Email.Trim(), request.Password);

                await AdminAuth.DefaultInstance.SetCustomserClaimAsync(credentials.User.Uid,
                    new Dictionary<string, object>
                    {
                        {"birthday", request.Birthday.ToString("yyyy-MM-dd") }
                    });
                //sign in token must include the birthday
                return await SignInAsync(new SignInRequest
                {
                    Email = request.Email,
                    Password = request.Password
                });
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

        public async Task<AuthResponse> SignInAsync(SignUpRequest request)
        {
            try
            {
                var credentials = await firebaseAuth.CreateUsersWithEmailAndPasswordAsync(request.Email.Trim(), request.Password);

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
    }
}