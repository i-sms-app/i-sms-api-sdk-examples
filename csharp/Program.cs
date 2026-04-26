using ISms;

var apiKey = Environment.GetEnvironmentVariable("ISMS_API_KEY") ?? "";
var client = new ISmsClient(apiKey);

Console.WriteLine("User info:");
Console.WriteLine(await client.GetUserInfoAsync());

var projects = await client.SearchProjectsAsync("深度求索");
Console.WriteLine("Project search result:");
Console.WriteLine(projects);

if (!projects.TryGetProperty("success", out var success) || !success.GetBoolean())
{
    return;
}

var data = projects.GetProperty("data");
if (data.GetArrayLength() == 0)
{
    return;
}

var project = data[0];
var numberResult = await client.GetNumberAsync(new GetNumberRequest
{
    ProjectId = JsonValueToString(project.GetProperty("project_id")),
    ProjectName = JsonValueToString(project.GetProperty("name")),
    ProjectToken = JsonValueToString(project.GetProperty("token")),
    Quantity = 1
});

Console.WriteLine("Get number result:");
Console.WriteLine(numberResult);

if (!numberResult.TryGetProperty("success", out var numberSuccess) || !numberSuccess.GetBoolean())
{
    return;
}

var numbers = numberResult.GetProperty("data");
if (numbers.GetArrayLength() == 0)
{
    return;
}

var firstNumber = numbers[0];
var orderId = firstNumber.TryGetProperty("orderId", out var camelOrderId)
    ? JsonValueToString(camelOrderId)
    : JsonValueToString(firstNumber.GetProperty("order_id"));

for (var i = 0; i < 12; i++)
{
    var sms = await client.GetSmsAsync(orderId: orderId);
    Console.WriteLine("SMS result:");
    Console.WriteLine(sms);
    if (sms.TryGetProperty("success", out var smsSuccess) && smsSuccess.GetBoolean())
    {
        break;
    }
    await Task.Delay(TimeSpan.FromSeconds(5));
}

Console.WriteLine("Release number:");
Console.WriteLine(await client.ReleaseNumberAsync(orderId: orderId));

static string JsonValueToString(System.Text.Json.JsonElement value)
{
    return value.ValueKind == System.Text.Json.JsonValueKind.String
        ? value.GetString() ?? ""
        : value.ToString();
}
