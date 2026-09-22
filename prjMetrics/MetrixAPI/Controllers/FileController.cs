using Microsoft.AspNetCore.Mvc;
using Azure.Storage.Blobs;

//Emeris School of Computer Science, 2025
namespace MetrixAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class FileController : ControllerBase
    {
        private readonly IConfiguration _configuration;


    public FileController(IConfiguration configuration)
        {
            _configuration = configuration;
        }

        //POST: api/File/upload-image
        [HttpPost("upload-image")]
        public async Task<IActionResult> UploadImage(IFormFile file)
        {
            if (file == null || file.Length == 0)
            {
                return BadRequest("No image was uploaded.");
            }

                //this is to read the connection string
                string connectionString = _configuration.GetConnectionString("AzureBlobStorage");
                string containerName = "images";

                BlobServiceClient blobServiceClient = new BlobServiceClient(connectionString);
                BlobContainerClient containerClient = blobServiceClient.GetBlobContainerClient(containerName);

                await containerClient.CreateIfNotExistsAsync(Azure.Storage.Blobs.Models.PublicAccessType.Blob);
                string fileName = $"{Guid.NewGuid()}_{file.FileName}";
               BlobClient blobClient =containerClient.GetBlobClient(fileName);

               using (var stream = file.OpenReadStream())
            {
                await blobClient.UploadAsync(stream, true);
            }

            return Ok(new
            {
                message = "MeTrix media uploaded successfully!",
                imageUrl = blobClient.Uri.ToString()
            });
        }
    }
}
/* 
References
Emeris School of Computer Science. 2025. All about the blob storage - CLDV6211.[video online] (Version 10.0) [Source Code] . Available at:< https://www.youtube.com/watch?v=tCROBkSoi3Y&list=PL480DYS-b_kevhFsiTpPIB2RzhKPig4iK&index=9>  [Accessed 16 September 2026].
*/