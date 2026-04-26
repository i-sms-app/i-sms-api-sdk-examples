<?php

require __DIR__ . '/ISmsClient.php';

$apiKey = getenv('ISMS_API_KEY') ?: '';
$client = new ISmsClient($apiKey);

echo "User info:\n";
print_r($client->getUserInfo());

$projects = $client->searchProjects('深度求索');
echo "Project search result:\n";
print_r($projects);

if (empty($projects['success']) || empty($projects['data'])) {
    exit;
}

$project = $projects['data'][0];
$numberResult = $client->getNumber(
    (string) $project['project_id'],
    (string) $project['name'],
    (string) $project['token'],
    1
);
echo "Get number result:\n";
print_r($numberResult);

if (empty($numberResult['success']) || empty($numberResult['data'])) {
    exit;
}

$orderId = $numberResult['data'][0]['orderId'] ?? $numberResult['data'][0]['order_id'] ?? null;
for ($i = 0; $i < 12; $i++) {
    $sms = $client->getSms($orderId);
    echo "SMS result:\n";
    print_r($sms);
    if (!empty($sms['success'])) {
        break;
    }
    sleep(5);
}

echo "Release number:\n";
print_r($client->releaseNumber($orderId));
