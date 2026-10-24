using MetrixAPI.Data;
using MetrixAPI.Models;
using Microsoft.EntityFrameworkCore;

namespace MetrixAPI.Services
{
    public class ClothingService
    {
        private readonly ApplicationDbContext _context;
        private readonly IUserIdentityService _identity;

        public ClothingService(
            ApplicationDbContext context,
            IUserIdentityService identity)
        {
            _context = context;
            _identity = identity;
        }

        public async Task<List<ClothingItem>> GetClothingAsync()
        {
            var userId = _identity.GetUserId();

            return await _context.ClothingItems
                .Where(x => x.UserId == userId && !x.IsWishlist)
                .OrderByDescending(x => x.CreatedAt)
                .ToListAsync();
        }

        public async Task<List<ClothingItem>> GetWishlistAsync()
        {
            var userId = _identity.GetUserId();

            return await _context.ClothingItems
                .Where(x => x.UserId == userId && x.IsWishlist)
                .OrderByDescending(x => x.CreatedAt)
                .ToListAsync();
        }

        public async Task<ClothingItem> AddClothingAsync(
            ClothingItem item)
        {
            item.Id = 0;
            item.UserId = _identity.GetUserId();
            item.IsWishlist = false;
            item.CreatedAt = DateTime.UtcNow;

            _context.ClothingItems.Add(item);

            await _context.SaveChangesAsync();

            return item;
        }

        public async Task<ClothingItem> AddWishlistAsync(
            ClothingItem item)
        {
            item.Id = 0;
            item.UserId = _identity.GetUserId();
            item.IsWishlist = true;
            item.CreatedAt = DateTime.UtcNow;

            _context.ClothingItems.Add(item);

            await _context.SaveChangesAsync();

            return item;
        }

        public async Task<bool> DeleteAsync(int id)
        {
            var userId = _identity.GetUserId();

            var item = await _context.ClothingItems
                .FirstOrDefaultAsync(
                    x => x.Id == id &&
                         x.UserId == userId);

            if (item == null)
            {
                return false;
            }

            var outfitLinks = await _context.OutfitItems
                .Where(oi => oi.ClothingItemId == id)
                .ToListAsync();

            _context.OutfitItems.RemoveRange(outfitLinks);
            _context.ClothingItems.Remove(item);

            await _context.SaveChangesAsync();

            return true;
        }
    }
}