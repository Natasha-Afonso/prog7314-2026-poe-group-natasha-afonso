using Microsoft.AspNetCore.Mvc;
using MetrixAPI.Models;

namespace MetrixAPI.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class TreasureMapController : Controller
    {

     /*   [HttpGet("booty")]
        public IActionResult DiscoverBooty([FromQuery] string passcode)
        {
            if (passcode != "blackbeard")
            {
                return Unauthorized("Avast! Ye lack the secret word.");
            }

\            var treasure = new Treasure
            {
                Coordinates = "25.0000° N, 71.0000° W",
                SecretMessage = "Dead men tell no tales, but this API does. Now try and figure ye new URL for Android!",
                GoldCoins = 500
            };

            // CONTROLLER CLUE 5: Ye can't send 'null' to a thirsty pirate! Return an OK (200) response containing the treasure payload.
            return Ok(treasure);
        }
        */
    }
}
