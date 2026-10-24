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
            [FromBody] ClothingItemRequest request)
        {
            var error = Validate(request, "Clothing");
            if (error != null)
            {
                return BadRequest(error);
            }

            return Ok(
                await _clothingService.AddClothingAsync(ToEntity(request)));
        }

        [HttpPost("wishlist")]
        public async Task<IActionResult> AddWishlist(
            [FromBody] ClothingItemRequest request)
        {
            var error = Validate(request, "Wishlist item");
            if (error != null)
            {
                return BadRequest(error);
            }


            return Ok(
                await _clothingService.AddWishlistAsync(ToEntity(request)));
        }

          private static string? Validate(ClothingItemRequest request, string label)
        {
            if (string.IsNullOrWhiteSpace(request.Name))
                return $"{label} name is required.";
            if (request.Name.Trim().Length > 100)
                return $"{label} name can be at most 100 characters.";
            if (string.IsNullOrWhiteSpace(request.Category) || request.Category.Trim().Length > 50)
                return "A category (maximum 50 characters) is required.";
            if (request.ImageUrl != null && request.ImageUrl.Length > 1000)
                return "Image URL is too long.";
            return null;
        }

        private static ClothingItem ToEntity(ClothingItemRequest request) =>
            new ClothingItem
            {
                Name = request.Name.Trim(),
                Category = request.Category.Trim(),
                ImageUrl = request.ImageUrl
            };

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