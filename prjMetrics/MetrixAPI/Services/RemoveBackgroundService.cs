using System.Net.Http.Headers;

namespace MetrixAPI.Services;

public interface IRemoveBackgroundService
{
    Task<byte[]> RemoveAsync(Stream image, string fileName, string contentType, CancellationToken cancellationToken = default);
}

public class RemoveBackgroundService : IRemoveBackgroundService
{
    private readonly HttpClient _http;
    private readonly IConfiguration _configuration;

    public RemoveBackgroundService(HttpClient http, IConfiguration configuration)
    {
        _http = http;
        _configuration = configuration;
    }




    public async Task<byte[]> RemoveAsync(Stream image, string fileName, string contentType, CancellationToken cancellationToken = default)
    {
        var apiKey = _configuration["RemoveBg:ApiKey"];
        if (string.IsNullOrWhiteSpace(apiKey))
            throw new InvalidOperationException("RemoveBg:ApiKey is not configured.");

        using var form = new MultipartFormDataContent();
        using var imageContent = new StreamContent(image);
        imageContent.Headers.ContentType = new MediaTypeHeaderValue(string.IsNullOrWhiteSpace(contentType) ? "image/jpeg" : contentType);
        form.Add(imageContent, "image_file", fileName);
        form.Add(new StringContent("auto"), "size");

        using var request = new HttpRequestMessage(HttpMethod.Post, "https://api.remove.bg/v1.0/removebg");
        request.Headers.Add("X-Api-Key", apiKey);
        request.Content = form;

        using var response = await _http.SendAsync(request, cancellationToken);
        if (!response.IsSuccessStatusCode)
        {
            var error = await response.Content.ReadAsStringAsync(cancellationToken);
            throw new InvalidOperationException($"Background removal failed ({(int)response.StatusCode}): {error}");
        }

        return await response.Content.ReadAsByteArrayAsync(cancellationToken);
    }
}
