using System;
using Microsoft.AspNetCore.Mvc;
using System.IdentityModel.Tokens.Jwt;
using Microsoft.IdentityModel.Tokens;
using Microsoft.AspNetCore.Authorization;
using MetrixAPI.Services;
using MetrixAPI.Helpers;
using MetrixAPI.Models;

//(TutorialsEU, 2022)
namespace MetrixAPI.Controllers
{
    //(Microsoft Learn, 2026)
    [Authorize] //limit access to authenticated users
    [ApiController]
    [Route("api/auth")]
    //(Watmore, 2018)
    public class AuthController(IAuthService authService) : ControllerBase
    {
        //(Microsoft Learn, 2026)
        [AllowAnonymous] //allow access too non-authenticated users
        [HttpPost("signup")]
        //post method sign up
        public async Task<IActionResult> SignUp(SignUpRequest request)
        {
            try
            {
                //creates the account with email and password from IAuthService
                var response = await authService.SignUpAsync(request);
                return StatusCode(StatusCodes.Status201Created, response); //201 Created for successfull sign up
            }
            catch (AppException ex) //calls the appException helper for the error message
            {
                return BadRequest(new { message = ex.Message });
            }
        }

        [AllowAnonymous] //(Microsoft Learn, 2026)
        [HttpPost("sso")]
        //google authentication ID is from JSON HTTP request payload
        public async Task<IActionResult> SignInWithGoogle([FromBody] GoogleSignInRequest request)
        {
            try
            {
                //user signs in with google then google ID matches to firebase ID
                var response = await authService.SignInWithGoogleAsync(request);
                return Ok(response);
            }
            catch (AppException ex) //calls the appException helper for the error message
            {
                return BadRequest(new { message = ex.Message });
            }
        }

        [AllowAnonymous] //(Microsoft Learn, 2026)
        [HttpPost("signin")]
        public async Task<IActionResult> SignIn(SignInRequest request)
        {
            var response = await authService.SignInAsync(request); //user signs in by entering email and password

            if (response == null) //error if email & password does not match firebase registered account
            {
                return BadRequest(new { message = "Email or Password is incorrect" });
            }
            return Ok(response);
        }

        [HttpGet("me")]
        public IActionResult Me() //needs valid JWT to work
        {
            // return basic user info (without password) and token to store client side
            return Ok(new
            {
                uid = User.FindFirst("user_id")?.Value,
                email = User.FindFirst("email")?.Value
            });
        }
    }
}

/*References
 
Microsoft Learn, 2026. Simple authorization in ASP.NET Core MVC. (Version 10.0) [Source Code]. Available at: < https://learn.microsoft.com/en-us/aspnet/core/mvc/security/authorization/simple?view=aspnetcore-10.0 > [Accessed 17 September 2026]
Watmore, J., 2018. Aspnet core registration login api. (Version 10.0) [Source Code]. Available at: < https://github.com/cornflourblue/aspnet-core-registration-login-api/blob/master/Controllers/UsersController.cs > [Accessed 17 September 2026]
TutorialsEU, 2022. Full stack developer course - using ASP.NET & Azure & C# & Android Jetpack Compose + Treblle. (Version 10.0) [Source Code]. Available at: < https://youtu.be/h1hwz_Y52po?si=fUPg4y2y9L8EuInT > [Accessed 17 September 2026]
 */