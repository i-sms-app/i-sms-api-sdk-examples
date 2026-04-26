using System.Text.Json;

namespace ISms;

public sealed class ISmsClient
{
    private readonly string _apiKey;
    private readonly string _baseUrl;
    private readonly HttpClient _httpClient;

    public ISmsClient(string apiKey, string baseUrl = "https://www.i-sms.app", HttpClient? httpClient = null)
    {
        if (string.IsNullOrWhiteSpace(apiKey))
        {
            throw new ArgumentException("apiKey is required", nameof(apiKey));
        }

        _apiKey = apiKey;
        _baseUrl = baseUrl.TrimEnd('/');
        _httpClient = httpClient ?? new HttpClient { Timeout = TimeSpan.FromSeconds(20) };
    }

    public Task<JsonElement> SearchProjectsAsync(string keyword, CancellationToken cancellationToken = default)
    {
        return GetAsync("/api/v2/projects", new Dictionary<string, string?>
        {
            ["keyword"] = keyword
        }, cancellationToken);
    }

    public Task<JsonElement> GetNumberAsync(GetNumberRequest request, CancellationToken cancellationToken = default)
    {
        return GetAsync("/api/v2/get_number", new Dictionary<string, string?>
        {
            ["project_id"] = request.ProjectId,
            ["project_name"] = request.ProjectName,
            ["project_token"] = request.ProjectToken,
            ["quantity"] = (request.Quantity <= 0 ? 1 : request.Quantity).ToString(),
            ["phone"] = request.Phone,
            ["province"] = request.Province,
            ["carrier"] = request.Carrier,
            ["ascription"] = request.Ascription
        }, cancellationToken);
    }

    public Task<JsonElement> GetSmsAsync(string? orderId = null, string? phoneNumber = null, string? projectId = null, CancellationToken cancellationToken = default)
    {
        return GetAsync("/api/v1/get_sms", new Dictionary<string, string?>
        {
            ["order_id"] = orderId,
            ["phone_number"] = phoneNumber,
            ["project_id"] = projectId
        }, cancellationToken);
    }

    public Task<JsonElement> ReleaseNumberAsync(string? orderId = null, string? phoneNumber = null, string? projectId = null, CancellationToken cancellationToken = default)
    {
        return GetAsync("/api/v1/release_number", new Dictionary<string, string?>
        {
            ["order_id"] = orderId,
            ["phone_number"] = phoneNumber,
            ["project_id"] = projectId
        }, cancellationToken);
    }

    public Task<JsonElement> GetUserInfoAsync(CancellationToken cancellationToken = default)
    {
        return GetAsync("/api/v1/user/info", new Dictionary<string, string?>(), cancellationToken);
    }

    private async Task<JsonElement> GetAsync(string path, Dictionary<string, string?> parameters, CancellationToken cancellationToken)
    {
        var query = string.Join("&", parameters
            .Where(item => !string.IsNullOrWhiteSpace(item.Value))
            .Select(item => $"{Uri.EscapeDataString(item.Key)}={Uri.EscapeDataString(item.Value!)}"));

        var url = $"{_baseUrl}{path}{(query.Length == 0 ? string.Empty : "?" + query)}";
        using var request = new HttpRequestMessage(HttpMethod.Get, url);
        request.Headers.Accept.ParseAdd("application/json");
        request.Headers.Add("X-API-KEY", _apiKey);

        using var response = await _httpClient.SendAsync(request, cancellationToken);
        var body = await response.Content.ReadAsStringAsync(cancellationToken);
        using var document = JsonDocument.Parse(body);
        return document.RootElement.Clone();
    }
}

public sealed class GetNumberRequest
{
    public string ProjectId { get; init; } = "";
    public string ProjectName { get; init; } = "";
    public string ProjectToken { get; init; } = "";
    public int Quantity { get; init; } = 1;
    public string? Phone { get; init; }
    public string? Province { get; init; }
    public string? Carrier { get; init; }
    public string? Ascription { get; init; }
}
