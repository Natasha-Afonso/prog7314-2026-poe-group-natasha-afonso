using System.Security.Claims;


namespace MetrixAPI.Services
{
    public class UserIdentityService : IUserIdentityService
    {
        private readonly IHttpContextAccessor _httpContextAccessor;

        public UserIdentityService(
            IHttpContextAccessor httpContextAccessor)
        {
            _httpContextAccessor = httpContextAccessor;
        }

        public string GetUserId()
        {
            var user = _httpContextAccessor.HttpContext?.User;

            if (user == null || user.Identity?.IsAuthenticated != true)
            {
                throw new UnauthorizedAccessException(
                    "The user is not authenticated.");
            }

            var userId =
                user.FindFirst(ClaimTypes.NameIdentifier)?.Value
                ?? user.FindFirst("user_id")?.Value
                ?? user.FindFirst("sub")?.Value;

            if (string.IsNullOrWhiteSpace(userId))
            {
                throw new UnauthorizedAccessException(
                    "The authenticated user does not have a valid ID.");
            }

            return userId;
        }
    }
}