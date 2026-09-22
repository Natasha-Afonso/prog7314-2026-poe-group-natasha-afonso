using MetrixAPI.Models;
using MetrixAPI.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
namespace MetrixAPI.Controllers
{
    [ApiController]
    [Route("api/clothing")]
    [Authorize]
    
    public class ClothingController : ControllerBase
    {
        private readonly ClothingService _clothingService;

        public ClothingController(
            ClothingService clothingService)
        {
            _clothingService = clothingService;
        }

        [HttpGet]
        public async Task<IActionResult> GetClothing()
        {
            return Ok(
                await _clothingService.GetClothingAsync());
        }

        [HttpGet("wishlist")]
        public async Task<IActionResult> GetWishlist()
        {
            return Ok(
                await _clothingService.GetWishlistAsync());
        }

        [HttpPost]
        public async Task<IActionResult> AddClothing(
            [FromBody] ClothingItem item)
        {
            if (string.IsNullOrWhiteSpace(item.Name))
            {
                return BadRequest("Clothing name is required.");
            }

            return Ok(
                await _clothingService.AddClothingAsync(item));
        }

        [HttpPost("wishlist")]
        public async Task<IActionResult> AddWishlist(
            [FromBody] ClothingItem item)
        {
            if (string.IsNullOrWhiteSpace(item.Name))
            {
                return BadRequest("Item name is required.");
            }

            return Ok(
                await _clothingService.AddWishlistAsync(item));
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteClothing(
            int id)
        {
            var deleted =
                await _clothingService.DeleteAsync(id);

            if (!deleted)
            {
                return NotFound();
            }

            return NoContent();
        }
    }
}