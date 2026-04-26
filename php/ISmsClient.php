<?php

class ISmsClient
{
    private string $apiKey;
    private string $baseUrl;
    private int $timeout;

    public function __construct(string $apiKey, string $baseUrl = 'https://www.i-sms.app', int $timeout = 20)
    {
        if ($apiKey === '') {
            throw new InvalidArgumentException('apiKey is required');
        }
        $this->apiKey = $apiKey;
        $this->baseUrl = rtrim($baseUrl, '/');
        $this->timeout = $timeout;
    }

    public function searchProjects(string $keyword): array
    {
        return $this->get('/api/v2/projects', ['keyword' => $keyword]);
    }

    public function getNumber(
        string $projectId,
        string $projectName,
        string $projectToken,
        int $quantity = 1,
        ?string $phone = null,
        ?string $province = null,
        ?string $carrier = null,
        ?string $ascription = null
    ): array {
        return $this->get('/api/v2/get_number', [
            'project_id' => $projectId,
            'project_name' => $projectName,
            'project_token' => $projectToken,
            'quantity' => $quantity,
            'phone' => $phone,
            'province' => $province,
            'carrier' => $carrier,
            'ascription' => $ascription,
        ]);
    }

    public function getSms(?string $orderId = null, ?string $phoneNumber = null, ?string $projectId = null): array
    {
        return $this->get('/api/v1/get_sms', [
            'order_id' => $orderId,
            'phone_number' => $phoneNumber,
            'project_id' => $projectId,
        ]);
    }

    public function releaseNumber(?string $orderId = null, ?string $phoneNumber = null, ?string $projectId = null): array
    {
        return $this->get('/api/v1/release_number', [
            'order_id' => $orderId,
            'phone_number' => $phoneNumber,
            'project_id' => $projectId,
        ]);
    }

    public function getUserInfo(): array
    {
        return $this->get('/api/v1/user/info');
    }

    private function get(string $path, array $params = []): array
    {
        $cleanParams = array_filter($params, static fn($value) => $value !== null && $value !== '');
        $url = $this->baseUrl . $path;
        if (!empty($cleanParams)) {
            $url .= '?' . http_build_query($cleanParams);
        }

        if (!function_exists('curl_init')) {
            return $this->getWithStream($url);
        }

        $ch = curl_init($url);
        curl_setopt_array($ch, [
            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_TIMEOUT => $this->timeout,
            CURLOPT_HTTPHEADER => [
                'Accept: application/json',
                'X-API-KEY: ' . $this->apiKey,
            ],
        ]);

        $body = curl_exec($ch);
        $httpStatus = (int) curl_getinfo($ch, CURLINFO_HTTP_CODE);
        $error = curl_error($ch);
        curl_close($ch);

        if ($body === false) {
            return ['success' => false, 'message' => $error ?: 'HTTP request failed'];
        }

        $data = json_decode($body, true);
        if (!is_array($data)) {
            $data = ['success' => false, 'message' => $body];
        }
        if ($httpStatus >= 400) {
            $data['http_status'] = $httpStatus;
        }
        return $data;
    }

    private function getWithStream(string $url): array
    {
        $context = stream_context_create([
            'http' => [
                'method' => 'GET',
                'timeout' => $this->timeout,
                'ignore_errors' => true,
                'header' => implode("\r\n", [
                    'Accept: application/json',
                    'X-API-KEY: ' . $this->apiKey,
                ]),
            ],
        ]);

        $body = @file_get_contents($url, false, $context);
        if ($body === false) {
            return ['success' => false, 'message' => 'HTTP request failed'];
        }

        $httpStatus = 0;
        if (isset($http_response_header[0]) && preg_match('/\s(\d{3})\s/', $http_response_header[0], $matches)) {
            $httpStatus = (int) $matches[1];
        }

        $data = json_decode($body, true);
        if (!is_array($data)) {
            $data = ['success' => false, 'message' => $body];
        }
        if ($httpStatus >= 400) {
            $data['http_status'] = $httpStatus;
        }
        return $data;
    }
}
