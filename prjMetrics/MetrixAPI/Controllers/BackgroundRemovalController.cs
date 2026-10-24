using System.Net;
using System.Net.Http.Headers;
using Azure.Storage.Blobs;
using Azure.Storage.Blobs.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace MetrixAPI.Controllers;

[ApiController]
[Route("api/clothing")]
[Authorize]
public class BackgroundRemovalController : ControllerBase
{
    private const long MaxBytes = 10 * 1024 * 1024;

    private readonly IHttpClientFactory _httpFactory;
    private readonly IConfiguration _config;
    private readonly BlobServiceClient _blobService;
    private readonly ILogger<BackgroundRemovalController> _logger;

    public BackgroundRemovalController(
        IHttpClientFactory httpFactory,
        IConfiguration config,
        BlobServiceClient blobService,
        ILogger<BackgroundRemovalController> logger)
    {
        _httpFactory = httpFactory;
        _config = config;
        _blobService = blobService;
        _logger = logger;
    }

    // POST api/clothing/remove-background
    [HttpPost("remove-background")]
    [RequestSizeLimit(MaxBytes)]
    public async Task<IActionResult> RemoveBackground(IFormFile file)
    {
        var apiKey = _config["RemoveBg:ApiKey"];
        if (string.IsNullOrWhiteSpace(apiKey))
            return StatusCode(503, "Background remover is not configured on the server.");

        if (file == null || file.Length == 0)
            return BadRequest("No image was uploaded.");
        if (file.Length > MaxBytes)
            return BadRequest("Image is too large (maximum 10 MB).");
        if (string.IsNullOrEmpty(file.ContentType) || !file.ContentType.StartsWith("image/", StringComparison.OrdinalIgnoreCase))
            return BadRequest("Only image files are allowed.");

        byte[] png;
        try
        {
            using var form = new MultipartFormDataContent();
            await using var input = file.OpenReadStream();
            var imageContent = new StreamContent(input);
            imageContent.Headers.ContentType = new MediaTypeHeaderValue(file.ContentType);
            form.Add(imageContent, "image_file", file.FileName);
            form.Add(new StringContent("auto"), "size");
            form.Add(new StringContent("png"), "format");

            using var request = new HttpRequestMessage(HttpMethod.Post, "https://api.remove.bg/v1.0/removebg")
            {
                Content = form
            };
            request.Headers.Add("X-Api-Key", apiKey);

            var http = _httpFactory.CreateClient();
            http.Timeout = TimeSpan.FromSeconds(60);
            using var response = await http.SendAsync(request);

            if (!response.IsSuccessStatusCode)
            {
                _logger.LogWarning("remove.bg returned {Status}", (int)response.StatusCode);
                // pass the useful ones through so the app can explain what happened
                return response.StatusCode switch
                {
                    HttpStatusCode.PaymentRequired => StatusCode(402, "Background remover has run out of credits."),
                    HttpStatusCode.TooManyRequests => StatusCode(429, "Background remover is busy, try again shortly."),
                    HttpStatusCode.Forbidden => StatusCode(502, "Background remover key was rejected by the provider."),
                    _ => StatusCode(502, "Background remover failed.")
                };
            }

            png = await response.Content.ReadAsByteArrayAsync();
        }
        catch (Exception ex) when (ex is HttpRequestException || ex is TaskCanceledException)
        {
            _logger.LogWarning(ex, "remove.bg request failed");
            return StatusCode(502, "Background remover could not be reached.");
        }

        //same container the normal image upload uses
        var container = _blobService.GetBlobContainerClient("images");
        await container.CreateIfNotExistsAsync(PublicAccessType.Blob);
        var blob = container.GetBlobClient($"{Guid.NewGuid()}_clothing.png");
        await blob.UploadAsync(new BinaryData(png), new BlobUploadOptions
        {
            HttpHeaders = new BlobHttpHeaders { ContentType = "image/png" }
        });

        return Ok(new
        {
            message = "Background removed.",
            imageUrl = blob.Uri.ToString()
        });
    }
}
