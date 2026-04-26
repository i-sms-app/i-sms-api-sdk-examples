import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Example {
    public static void main(String[] args) throws Exception {
        String apiKey = System.getenv("ISMS_API_KEY");
        ISmsClient client = new ISmsClient(apiKey);

        System.out.println("User info:");
        System.out.println(client.getUserInfo());

        System.out.println("Project search result:");
        String projectsJson = client.searchProjects("深度求索");
        System.out.println(projectsJson);

        String firstProject = firstDataObject(projectsJson);
        if (firstProject == null) {
            return;
        }

        ISmsClient.GetNumberRequest request = new ISmsClient.GetNumberRequest();
        request.projectId = jsonValue(firstProject, "project_id");
        request.projectName = jsonValue(firstProject, "name");
        request.projectToken = jsonValue(firstProject, "token");
        request.quantity = 1;

        String numberJson = client.getNumber(request);
        System.out.println("Get number result:");
        System.out.println(numberJson);

        String firstNumber = firstDataObject(numberJson);
        if (firstNumber == null) {
            return;
        }

        String orderId = jsonValue(firstNumber, "orderId");
        if (orderId == null || orderId.isBlank()) {
            orderId = jsonValue(firstNumber, "order_id");
        }
        if (orderId == null || orderId.isBlank()) {
            return;
        }

        for (int i = 0; i < 12; i++) {
            String smsJson = client.getSms(orderId);
            System.out.println("SMS result:");
            System.out.println(smsJson);
            if (smsJson.contains("\"success\":true") || smsJson.contains("\"success\": true")) {
                break;
            }
            Thread.sleep(5000);
        }

        System.out.println("Release number:");
        System.out.println(client.releaseNumber(orderId));
    }

    private static String firstDataObject(String json) {
        int dataIndex = json.indexOf("\"data\"");
        if (dataIndex < 0) {
            return null;
        }
        int arrayStart = json.indexOf('[', dataIndex);
        if (arrayStart < 0) {
            return null;
        }
        int objectStart = json.indexOf('{', arrayStart);
        if (objectStart < 0) {
            return null;
        }
        int depth = 0;
        boolean inString = false;
        boolean escaped = false;
        for (int i = objectStart; i < json.length(); i++) {
            char ch = json.charAt(i);
            if (inString) {
                if (escaped) {
                    escaped = false;
                } else if (ch == '\\') {
                    escaped = true;
                } else if (ch == '"') {
                    inString = false;
                }
                continue;
            }
            if (ch == '"') {
                inString = true;
            } else if (ch == '{') {
                depth++;
            } else if (ch == '}') {
                depth--;
                if (depth == 0) {
                    return json.substring(objectStart, i + 1);
                }
            }
        }
        return null;
    }

    private static String jsonValue(String jsonObject, String field) {
        Pattern pattern = Pattern.compile("\"" + Pattern.quote(field) + "\"\\s*:\\s*(\"((?:\\\\.|[^\"])*)\"|[-\\w.]+)");
        Matcher matcher = pattern.matcher(jsonObject);
        if (!matcher.find()) {
            return null;
        }
        String quoted = matcher.group(2);
        if (quoted != null) {
            return quoted.replace("\\\"", "\"").replace("\\\\", "\\");
        }
        return matcher.group(1);
    }
}
