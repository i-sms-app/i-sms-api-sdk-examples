import json
import urllib.parse
import urllib.request


class ISmsClient:
    def __init__(self, api_key, base_url="https://www.i-sms.app", timeout=20):
        if not api_key:
            raise ValueError("api_key is required")
        self.api_key = api_key
        self.base_url = base_url.rstrip("/")
        self.timeout = timeout

    def search_projects(self, keyword):
        return self._get("/api/v2/projects", {"keyword": keyword})

    def get_number(
        self,
        project_id,
        project_name,
        project_token,
        quantity=1,
        phone=None,
        province=None,
        carrier=None,
        ascription=None,
    ):
        return self._get(
            "/api/v2/get_number",
            {
                "project_id": project_id,
                "project_name": project_name,
                "project_token": project_token,
                "quantity": quantity,
                "phone": phone,
                "province": province,
                "carrier": carrier,
                "ascription": ascription,
            },
        )

    def get_sms(self, order_id=None, phone_number=None, project_id=None):
        return self._get(
            "/api/v1/get_sms",
            {
                "order_id": order_id,
                "phone_number": phone_number,
                "project_id": project_id,
            },
        )

    def release_number(self, order_id=None, phone_number=None, project_id=None):
        return self._get(
            "/api/v1/release_number",
            {
                "order_id": order_id,
                "phone_number": phone_number,
                "project_id": project_id,
            },
        )

    def get_user_info(self):
        return self._get("/api/v1/user/info")

    def _get(self, path, params=None):
        query = self._clean_params(params or {})
        url = f"{self.base_url}{path}"
        if query:
            url = f"{url}?{urllib.parse.urlencode(query)}"

        request = urllib.request.Request(url, headers={"X-API-KEY": self.api_key})
        try:
            with urllib.request.urlopen(request, timeout=self.timeout) as response:
                body = response.read().decode("utf-8")
                return json.loads(body)
        except urllib.error.HTTPError as exc:
            body = exc.read().decode("utf-8")
            try:
                payload = json.loads(body)
            except json.JSONDecodeError:
                payload = {"success": False, "message": body}
            payload["http_status"] = exc.code
            return payload

    @staticmethod
    def _clean_params(params):
        return {
            key: value
            for key, value in params.items()
            if value is not None and value != ""
        }
