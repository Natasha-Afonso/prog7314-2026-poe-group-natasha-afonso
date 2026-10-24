using MetrixAPI.Data;
using MetrixAPI.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace MetrixAPI.Controllers
{
    //(Emeris School of Computer Science, 2025)
    [Authorize]
    [ApiController]
    [Route("api/biometric")]
    public class BiometricController : ControllerBase
    {
        private string? CurrentUserId => User.FindFirst("user_id")?.Value; //gets current user with correct JWT
        private readonly ApplicationDbContext _context;
        private const int MaxFailedAttempts = 5;
        private static readonly TimeSpan LockoutDuration = TimeSpan.FromMinutes(15);
        private readonly IPasswordHasher<Biometric> _hasher;

        public BiometricController(ApplicationDbContext context, IPasswordHasher<Biometric> hasher)
        {
            _context = context;
            _hasher = hasher;
        }

        //(Shah, 2021)
        //hashset stores all the easy passwords the user cannot set as a password
        private static readonly HashSet<string> WeakPins = new()
        {
            "0000","1111","2222","3333","4444","5555","6666","7777","8888","9999","1234","4321"
        };

        //method checks if user has a PIN and if biometrics is enabled
        [HttpGet]
        public async Task<ActionResult<BiometricStatusResponse>> GetStatus()
        {
            var uid = CurrentUserId;
            if (uid is null)
            {
                return Unauthorized();
            }

            var biometric = await _context.Biometric.AsNoTracking().FirstOrDefaultAsync(b => b.UserId == uid);
            return Ok(new BiometricStatusResponse
            {
                HasPin = biometric?.PinHash != null,
                BiometricEnabled = biometric?.BiometricEnabled ?? false
            });
        }

        //method to create a pin or change a PIN
        [HttpPut("pin")]
        public async Task<ActionResult<BiometricResponse>> SavePin(SavePinRequest dto)
        {
            var uid = CurrentUserId;
            if (uid is null)
            {
                return Unauthorized();
            }

            //if users entered pin is weak (listed in hashset) then they are prompted for a new one
            if (WeakPins.Contains(dto.NewPin))
            {
                return Ok(new BiometricResponse
                {
                    Success = false,
                    Message = "That PIN is too easy to guess. Choose another."
                });
            }

            var biometric = await _context.Biometric.FirstOrDefaultAsync(b => b.UserId == uid);
            if (biometric is null)
            {
                biometric = new Biometric { UserId = uid };
                _context.Biometric.Add(biometric);
                await _context.SaveChangesAsync();
            }

            //new PIN is hashed and saved, failed attempts and lockout are reset
            biometric.PinHash = _hasher.HashPassword(biometric, dto.NewPin);
            biometric.FailedAttempts = 0;
            biometric.LockedoutEndUtc = null;
            biometric.UpdatedAt = DateTime.UtcNow;
            await _context.SaveChangesAsync();

            return Ok(new BiometricResponse { Success = true });
        }

        //user enters app with pin instead of biometrics
        [HttpPost("pin/verify")]
        public async Task<ActionResult<BiometricResponse>> VerifyPin(VerifyPinRequest dto)
        {
            var uid = CurrentUserId;
            if (uid is null)
            {
                return Unauthorized();
            }

            var biometric = await _context.Biometric.FirstOrDefaultAsync(b => b.UserId == uid);
            if (biometric?.PinHash is null)
            {
                return Ok(new BiometricResponse
                {
                    Success = false,
                    Message = "No PIN has been set."
                });
            }

            if (IsLocked(biometric, out var secondsLeft))
            {
                return Ok(LockedResult(secondsLeft));
            }

            return await CheckPinAsync(biometric, dto.Pin) ? Ok(new BiometricResponse { Success = true }) : Ok(FailedResult(biometric));
        }

        //enabling and disabling the biometric in settings
        [HttpPut("biometric")]
        public async Task<ActionResult<BiometricStatusResponse>> SetBiometric(BiometricRequest dto)
        {
            var uid = CurrentUserId;
            if (uid is null)
            {
                return Unauthorized();
            }

            var biometric = await _context.Biometric.FirstOrDefaultAsync(b => b.UserId == uid);
            if (biometric is null)
            {
                biometric = new Biometric { UserId = uid };
                _context.Biometric.Add(biometric);
                await _context.SaveChangesAsync();
            }

            // biometrics must always have a PIN as the fallback
            if (dto.Enabled && biometric.PinHash is null)
            {
                return BadRequest(new { message = "Set a PIN before enabling biometric login." });
            }

            //biometrics is successfully enabled after PIN created
            biometric.BiometricEnabled = dto.Enabled;
            biometric.UpdatedAt = DateTime.UtcNow;
            await _context.SaveChangesAsync();

            return Ok(new BiometricStatusResponse
            {
                HasPin = biometric.PinHash != null,
                BiometricEnabled = biometric.BiometricEnabled
            });
        }

        //checks if the user is still locked out and how many seconds are left
        private static bool IsLocked(Biometric biometric, out int secondsLeft)
        {
            //(StackOverflow, 2024)
            secondsLeft = 0;
            if (biometric.LockedoutEndUtc is DateTime end && end > DateTime.UtcNow)
            {
                secondsLeft = (int)Math.Ceiling((end - DateTime.UtcNow).TotalSeconds); //rounds of to next highest whole number
                return true;
            }
            return false;
        }

        // Verifies the PIN and updates the failed-attempt recorder
        //(.NET Foundation, 2024)
        private async Task<bool> CheckPinAsync(Biometric biometric, string pin)
        {
            var result = _hasher.VerifyHashedPassword(biometric, biometric.PinHash!, pin);

            if (result == PasswordVerificationResult.Failed)
            {
                biometric.FailedAttempts++;
                if (biometric.FailedAttempts >= MaxFailedAttempts)
                {
                    biometric.LockedoutEndUtc = DateTime.UtcNow.Add(LockoutDuration);
                    biometric.FailedAttempts = 0;
                }
                await _context.SaveChangesAsync();
                return false;
            }

            if (result == PasswordVerificationResult.SuccessRehashNeeded)
            {
                biometric.PinHash = _hasher.HashPassword(biometric, pin); // upgrade to the current hash settings
            }

            biometric.FailedAttempts = 0;
            biometric.LockedoutEndUtc = null;
            await _context.SaveChangesAsync();
            return true;
        }

        //if user is locked out then they are returned with an error message and how long they must wait (15 minutes)
        private static BiometricResponse LockedResult(int secondsLeft) => new()
        {
            Success = false,
            Locked = true,
            RetryAfterSeconds = secondsLeft,
            Message = "Too many incorrect attempts. Try again later."
        };

        private static BiometricResponse FailedResult(Biometric biometric)
        {
            //(StackOverflow, 2024)
            //counts the number of failed results too see if it should call the lockedResult method
            if (biometric.LockedoutEndUtc is DateTime end && end > DateTime.UtcNow)
            {
                return LockedResult((int)Math.Ceiling((end - DateTime.UtcNow).TotalSeconds)); //rounds of to next highest whole number
            }
            //if incorrect pins attempt is under 5 tries then they get normal error message and can try again
            return new BiometricResponse
            {
                Success = false,
                AttemptsRemaining = MaxFailedAttempts - biometric.FailedAttempts,
                Message = "Incorrect PIN."
            };
        }
    }
}

/*References
 *
 * .NET Foundation. 2024. PasswordHasher.cs (Microsoft.Extensions.Identity.Core). Available at: < https://source.dot.net/Microsoft.Extensions.Identity.Core/PasswordHasher.cs.html > [Accessed 24 October 2026]
 * IIE Emeris School of Computer Science. 2025. Getting strated with MVC, Entity Framework and SQL Azure database for CLDV6211 Part 3. (Version 8.0-SNAPSHOT) [Source code]. Available at: < https://youtu.be/qkQ3YiKN5nk?si=9Zr8iT7DN8yE6x6C > [Accessed 20 September 2026].
 * Shah, K., 2021. C# - Hashset with example. (Version 10.0) [Source Code]. Available at: < https://www.c-sharpcorner.com/article/c-sharp-hashset-with-example?srsltid=AYCX9vIj_sppbPCtnQIqEd4MzqoLFmmA4XnaqznfkIK_UeSUoTczUsUT > [Accessed 24 October 2026]
 * StackOverflow, 2024. Calculate Time Remaining. (Version 10.0) [Source Code]. Available at: < https://stackoverflow.com/questions/473355/calculate-time-remaining > [Accessed 24 October 2026]
 */