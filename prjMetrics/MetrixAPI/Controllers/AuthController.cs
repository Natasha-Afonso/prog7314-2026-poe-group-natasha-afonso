using System;
using System.Collections.Generic;
using Microsoft.AspNetCore.Mvc;
using AutoMapper;
using System.IdentityModel.Tokens.Jwt;
using WebApi.Helpers;
using Microsoft.Extensions.Options;
using System.Text;
using Microsoft.IdentityModel.Tokens;
using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using WebApi.Services;
using MetrixAPI.Helpers;
using MetrixAPI.Models;

namespace MetrixAPI.Controllers
{
    [Authorize]
    [ApiController]
    [Route("api/auth")]
    public class AuthController(IAuthService authService) : ControllerBase
    {
        [AllowAnonymous]
        [HttpPost("signup")]
        public async Task<IActionResult> SignUp(SignUpRequest request)
        {
            try
            {
                var response = await authService.SignUpAsync(request);
                return StatusCode(StatusCode.Status201Created, response);
            }
            catch (AppException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }

        [AllowAnonymous]
        [HttpPost("sso")]
        public async Task<IActionResult> SignInWithGoogle([FromBody] GoogleSignInRequest request)
        {
            try
            {
                var response = await authService.SignInWithGoogleAsync(request);
                return Ok(response);
            }
            catch (AppException ex)
            {
                return BadRequest(new { message = ex.Message });
            }
        }

        [AllowAnonymous]
        [HttpPost("signin")]
        public async Task<IActionResult> SignIn(SignInRequest request)
        {
            var response = await authService.SignInAsync(request);

            if (response == null)
            {
                return BadRequest(new { message = "Email or Password is incorrect" });
            }
            return Ok(response);
        }

        [HttpGet("me")]
        public IActionResult Me()
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