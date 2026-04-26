import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

public class ISmsClient {
    private final String apiKey;
    private final String baseUrl;
    private final HttpClient httpClient;

    public ISmsClient(String apiKey) {
        this(apiKey, "https://www.i-sms.app");
    }

    public ISmsClient(String apiKey, String baseUrl) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("apiKey is required");
        }
        this.apiKey = apiKey;
        this.baseUrl = trimRightSlash(baseUrl);
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();
    }

    public String searchProjects(String keyword) throws IOException, InterruptedException {
        return get("/api/v2/projects", Map.of("keyword", keyword));
    }

    public String getNumber(GetNumberRequest request) throws IOException, InterruptedException {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("project_id", request.projectId);
        params.put("project_name", request.projectName);
        params.put("project_token", request.projectToken);
        params.put("quantity", String.valueOf(request.quantity <= 0 ? 1 : request.quantity));
        params.put("phone", request.phone);
        params.put("province", request.province);
        params.put("carrier", request.carrier);
        params.put("ascription", request.ascription);
        return get("/api/v2/get_number", params);
    }

    public String getSms(String orderId) throws IOException, InterruptedException {
        return getSms(orderId, null, null);
    }

    public String getSms(String orderId, String phoneNumber, String projectId) throws IOException, InterruptedException {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("order_id", orderId);
        params.put("phone_number", phoneNumber);
        params.put("project_id", projectId);
        return get("/api/v1/get_sms", params);
    }

    public String releaseNumber(String orderId) throws IOException, InterruptedException {
        return releaseNumber(orderId, null, null);
    }

    public String releaseNumber(String orderId, String phoneNumber, String projectId) throws IOException, InterruptedException {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("order_id", orderId);
        params.put("phone_number", phoneNumber);
        params.put("project_id", projectId);
        return get("/api/v1/release_number", params);
    }

    public String getUserInfo() throws IOException, InterruptedException {
        return get("/api/v1/user/info", Map.of());
    }

    private String get(String path, Map<String, String> params) throws IOException, InterruptedException {
        String query = buildQuery(params);
        String url = baseUrl + path + (query.isEmpty() ? "" : "?" + query);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(20))
                .header("Accept", "application/json")
                .header("X-API-KEY", apiKey)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return response.body();
    }

    private static String buildQuery(Map<String, String> params) {
        StringJoiner joiner = new StringJoiner("&");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String value = entry.getValue();
            if (value != null && !value.isBlank()) {
                joiner.add(encode(entry.getKey()) + "=" + encode(value));
            }
        }
        return joiner.toString();
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static String trimRightSlash(String value) {
        return value == null ? "" : value.replaceAll("/+$", "");
    }

    public static class GetNumberRequest {
        public String projectId;
        public String projectName;
        public String projectToken;
        public int quantity = 1;
        public String phone;
        public String province;
        public String carrier;
        public String ascription;
    }
}
