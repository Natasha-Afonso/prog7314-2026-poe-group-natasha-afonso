using Microsoft.AspNetCore.Mvc;
using Azure.Storage.Blobs;
using Microsoft.AspNetCore.Authorization;
using MetrixAPI.Services;
using Azure.Storage.Blobs.Models;

//Emeris School of Computer Science, 2025
namespace MetrixAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    [Authorize]
    public class FileController : ControllerBase
    {
        private readonly IConfiguration _configuration;
        private readonly IRemoveBackgroundService _removeBackground;


        public FileController(IConfiguration configuration, IRemoveBackgroundService removeBackground)
        {
            _configuration = configuration;
            _removeBackground = removeBackground;
        }

        //POST: api/File/upload-image
        [HttpPost("upload-image")]
        public async Task<IActionResult> UploadImage(IFormFile file, [FromQuery] bool removeBackground = false, CancellationToken cancellationToken = default)
        {
            if (file == null || file.Length == 0)
            {
                return BadRequest("No image was uploaded.");
            }
            if (!file.ContentType.StartsWith("image/", StringComparison.OrdinalIgnoreCase))
            {
                return BadRequest("Only image files are allowed.");
            }

            var connectionString = _configuration.GetConnectionString("AzureBlobStorage");
            if (string.IsNullOrWhiteSpace(connectionString))
            {

                return Problem("Azure Blob Storage is not configured.");
            }

            var containerClient = new BlobContainerClient(connectionString, "images");
            await containerClient.CreateIfNotExistsAsync(PublicAccessType.Blob, cancellationToken: cancellationToken);

            byte[] bytes;
            string extension;
            if (removeBackground)
            {
                await using var stream = file.OpenReadStream();
                bytes = await _removeBackground.RemoveAsync(stream, file.FileName, file.ContentType, cancellationToken);
                extension = ".png";
            }
            else
            {
                await using var stream = file.OpenReadStream();
                using var memory = new MemoryStream();
                await stream.CopyToAsync(memory, cancellationToken);
                bytes = memory.ToArray();
                extension = Path.GetExtension(file.FileName);
                if (string.IsNullOrWhiteSpace(extension)) extension = ".jpg";
            }

            var blob = containerClient.GetBlobClient($"{Guid.NewGuid():N}{extension}");

            await using var uploadStream = new MemoryStream(bytes);

            var uploadOptions = new BlobUploadOptions
            {
                HttpHeaders = new BlobHttpHeaders
                {
                    ContentType = removeBackground
                        ? "image/png"
                        : file.ContentType
                }
            };

            await blob.UploadAsync(uploadStream, uploadOptions, cancellationToken);

            return Ok(new
            {
                message = "Image uploaded successfully.",
                imageUrl = blob.Uri.ToString()
            });
        }
    }
}

/* 
References
Emeris School of Computer Science. 2025. All about the blob storage - CLDV6211.[video online] (Version 10.0) [Source Code] . Available at:< https://www.youtube.com/watch?v=tCROBkSoi3Y&list=PL480DYS-b_kevhFsiTpPIB2RzhKPig4iK&index=9>  [Accessed 16 September 2026].
*/