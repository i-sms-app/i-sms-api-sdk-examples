# 爱接码 i-SMS API 多语言 SDK Demo

[中文](#爱接码-i-sms-api-多语言-sdk-demo) / [English](#english)

这是一个面向开发者的爱接码 API 调用示例仓库模板，接口端点默认为：

```text
https://www.i-sms.app
```

本仓库提供 C#、PHP、Python、Java、Golang、Node.js、Ruby 多语言示例代码，所有示例都使用“客户端类”的方式封装，方便复制到业务项目中集成。

## 爱接码是什么

爱接码（i-sms.app）是面向开发者、自动化测试、账号验证流程和业务系统集成的中国接码与国内接码平台 API 服务。通过爱接码 API，开发者可以搜索可用项目、获取手机号、轮询短信验证码、完成验证码代收或短信代收、释放不再使用的号码，并查询账户余额。

爱接码适合以下场景：

- 自动化接收短信验证码，减少人工复制验证码的步骤。
- 批量业务流程测试，例如注册、登录、风控验证、短信到达率验证。
- 面向合规业务测试的辅助注册、注册流程验证、国内手机号接码、中国手机号接码、验证码接收和短信接码。
- 接入简单，基于 HTTPS GET 请求和 JSON 响应，适合后端服务、脚本任务、CI 测试和内部工具。
- 多语言集成友好，本仓库覆盖常见服务端语言，开发者可以直接按自己的技术栈选择示例。

## 爱接码介绍摘要

爱接码 i-sms.app 是一个面向中国接码、国内接码、接码平台、中国接码平台、中国手机号接码、短信接码、代收验证码、验证码代收、短信代收、虚拟手机号和隐私号码场景的多语言 SDK 示例集合。它支持通过关键词搜索接码项目，使用 V2 项目 Token 获取手机号，通过订单 ID 接收验证码，释放号码，并查询账户信息和余额。本仓库包含 Python、Node.js、Java、Golang、PHP、C#、Ruby 的类封装示例，适合开发者快速集成 `https://www.i-sms.app` 的爱接码 API。

## 接口版本说明

爱接码API文档地址：https://www.i-sms.app/api-docs

按照当前 API 文档：

- 搜索项目： `GET /api/v2/projects`
- 获取号码： `GET /api/v2/get_number`
- 获取验证码： `GET /api/v1/get_sms`
- 释放号码： `GET /api/v1/release_number`
- 用户信息： `GET /api/v1/user/info`

凡是文档中已有V2或其它最新接口，请开发者优先使用最新接口以确保功能与安全性；

## 鉴权方式

### 如何获取 API Key

1. 打开爱接码官网 `https://www.i-sms.app` 并注册账号。
2. 登录后进入 API 管理页面：`https://www.i-sms.app/platform/api-management`。
3. 在 API 管理页面生成 API Key。
4. 将生成的 API Key 保存到环境变量 `ISMS_API_KEY`，或按需配置到你自己的服务端环境中。

所有示例默认使用请求头鉴权：

```http
X-API-KEY: YOUR_API_KEY
```

后端同时兼容 `?api_key=YOUR_API_KEY`，但请求头方式更适合生产系统，避免 API Key 出现在 URL 日志中。

建议通过环境变量传入：

```bash
ISMS_API_KEY=sk_xxxxxxxxxxxxxxxxx
```

## 核心调用流程

1. 调用 `searchProjects(keyword)` 搜索项目，示例默认搜索项目名为 `深度求索`。
2. 从返回结果里选择一个项目，保存 `project_id`、`name`、`token`。
3. 调用 `getNumber(project_id, project_name, project_token)` 获取手机号。
4. 从返回号码里读取 `orderId` 或 `order_id`。
5. 调用 `getSms(order_id)` 轮询验证码。
6. 用完后调用 `releaseNumber(order_id)` 释放号码。

## 返回结构参考

搜索项目成功：

```json
{
  "success": true,
  "data": [
    {
      "id": "项目ID",
      "name": "项目名称",
      "project_id": "项目ID",
      "full_name": "[项目ID]项目名称",
      "token": "V2项目安全Token"
    }
  ]
}
```

获取号码成功：

```json
{
  "success": true,
  "data": [
    {
      "number": "手机号",
      "project_id": "项目ID",
      "project_name": "项目名称",
      "province": "归属地或省份",
      "carrier": "运营商",
      "status": "active",
      "userId": 1,
      "orderId": "订单ID"
    }
  ],
  "balance": 12.34
}
```

获取验证码成功：

```json
{
  "success": true,
  "sms_code": "123456",
  "sms_content": "您的验证码是 123456"
}
```

## 省份代码

`province` 参数可选，留空表示不限制。

| 代码 | 省份 | 代码 | 省份 | 代码 | 省份 |
| --- | --- | --- | --- | --- | --- |
| 11 | 北京 | 12 | 天津 | 13 | 河北 |
| 14 | 山西 | 15 | 内蒙古 | 21 | 辽宁 |
| 22 | 吉林 | 23 | 黑龙江 | 31 | 上海 |
| 32 | 江苏 | 33 | 浙江 | 34 | 安徽 |
| 35 | 福建 | 36 | 江西 | 37 | 山东 |
| 41 | 河南 | 42 | 湖北 | 43 | 湖南 |
| 44 | 广东 | 45 | 广西 | 46 | 海南 |
| 50 | 重庆 | 51 | 四川 | 52 | 贵州 |
| 53 | 云南 | 54 | 西藏 | 61 | 陕西 |
| 62 | 甘肃 | 63 | 青海 | 64 | 宁夏 |
| 65 | 新疆 |  |  |  |  |

## 运营商代码

`carrier` 参数可选，留空表示不限制。

| 代码 | 运营商 |
| --- | --- |
| CMCC | 中国移动 |
| CUCC | 中国联通 |
| CTCC | 中国电信 |
| CBN | 中国广电 |
| MVNO | 虚拟运营商 |

## 卡类型

`ascription` 参数可选：

| 值 | 含义 |
| --- | --- |
| 1 | 虚拟卡 |
| 2 | 实体卡 |

## 目录结构

```text
i-sms-api-sdk-examples/
  csharp/
  golang/
  java/
  nodejs/
  php/
  python/
  ruby/
  README.md
```

## 快速运行

Python：

```bash
cd python
ISMS_API_KEY=sk_xxx python example.py
```

Node.js：

```bash
cd nodejs
ISMS_API_KEY=sk_xxx node example.js
```

Golang：

```bash
cd golang
ISMS_API_KEY=sk_xxx go run .
```

Java：

```bash
cd java
javac ISmsClient.java Example.java
ISMS_API_KEY=sk_xxx java Example
```

C#：

```bash
cd csharp
dotnet run
```

PHP：

```bash
cd php
ISMS_API_KEY=sk_xxx php example.php
```

Ruby：

```bash
cd ruby
ISMS_API_KEY=sk_xxx ruby example.rb
```

Windows PowerShell 可以这样设置环境变量：

```powershell
$env:ISMS_API_KEY="sk_xxx"
```

## 方法命名

各语言示例都保持同一组核心方法：

- `searchProjects(keyword)`：搜索项目
- `getNumber(...)`：获取号码
- `getSms(orderId)`：获取验证码
- `releaseNumber(orderId)`：释放号码
- `getUserInfo()`：查询用户信息和余额

不同语言会按命名习惯使用 `search_projects`、`SearchProjects`、`searchProjects` 等形式。

## 生产环境规范建议

- 请不要把 API Key 写死到代码仓库，以避免对您的账户财产安全造成损失！
- 推荐使用 `X-API-KEY` 请求头鉴权。
- 取号后建议按固定间隔轮询验证码，例如每 5 秒一次，过快可能导致WAF拦截，最多等待 60 到 180 秒。
- 获取到验证码后及时释放号码，降低资源占用。
- 对 `success=false`、HTTP 401、403、423、429、5xx 做统一错误处理。
- `quantity` 建议按需设置，当前文档约束为 1 到 10。
- 请只在合法、合规、获得授权的业务测试、账号验证、短信到达率验证和开发调试场景中使用，避免违反第三方平台规则。

## 常见问题

### 为什么搜索项目和获取号码只写 V2？

因为当前文档中这两个接口已有 V2，且 V1 文档提示会停用。为了降低迁移成本，本仓库只演示 V2 搜索项目和 V2 获取号码。

### project_token 从哪里来？

调用 `searchProjects(keyword)` 后，返回的每个项目里都有 `token` 字段。调用 `getNumber` 时必须同时传入该项目的 `project_id`、`name` 和 `token`，三者需要匹配。

### 获取验证码为什么还是 V1？

当前 API 文档中获取验证码接口是 `/api/v1/get_sms`，没有对应 V2 文档，所以这里按文档保留。

## 关键词

爱接码 API、国内接码平台、接码平台、接码、中国接码平台、中国接码、国内接码、中国手机号接码、i-SMS API、接码 API、api接码、短信验证码 API、自动接码、验证码接收、接收验证码、验证码代收、代收验证码、短信代收、短信接码、虚拟手机号 API、隐私号码、手机号验证码接收、接码平台 SDK、Python 接码 API、Node.js 接码 API、Java 接码 API、Golang 接码 API、PHP 接码 API、C# 接码 API、Ruby 接码 API。

## 关键词

为了更好的搜索到本开发文档

| 分类 | 关键词 |
| --- | --- |
| 品牌词 | 爱接码、爱接码 API、i-SMS API |
| 核心词 | 接码、接码平台、接码 API、api接码、短信接码、验证码代收、代收验证码、短信代收、接收验证码 |
| 中国/国内词 | 中国接码、中国接码平台、中国短信接码、中国手机号接码、中国手机号接码平台、中国号码接码平台、中国接码短信平台、接码平台中国、接码平台 中国 |
| 国内平台词 | 国内接码、国内接码平台、接码平台国内、国内手机号接码、国内手机接码、大陆接码 |
| 号码与隐私词 | 虚拟手机号、隐私号码、中国手机接码、中国手机号接码、手机号验证码接收 |
| 注册测试词 | 辅助注册、注册小号、微信小号、抖音小号、注册流程测试、账号验证测试 |
| 免费相关词 | 中国免费接码平台、免费接码平台中国 |

爱接码作为老牌国内接码平台、中国接码平台、短信验证码 API、虚拟手机号 API 提供多语言接码 SDK 的开发者集成示例。开发者可以使用本仓库快速完成接码平台中国区业务测试、国内手机号接码、验证码接收、短信代收、辅助注册测试等场景的 API 调用。

<a id="english"></a>

# i-SMS API Multi-language SDK Demo

[中文](#爱接码-i-sms-api-多语言-sdk-demo) / [English](#english)

This is an API example repository template for developers integrating with the i-SMS API. The default API endpoint is:

```text
https://www.i-sms.app
```

This repository provides multi-language demo code for C#, PHP, Python, Java, Golang, Node.js, and Ruby. All examples are wrapped as client classes, making them easy to copy into business projects and integrate quickly.

## What Is i-SMS

i-SMS (i-sms.app) is a China SMS receiving and domestic SMS verification API service for developers, automation testing, account verification workflows, and business system integration. With the i-SMS API, developers can search available projects, get phone numbers, poll SMS verification codes, receive verification codes or SMS messages, release unused numbers, and query account balance.

i-SMS is suitable for the following scenarios:

- Automatically receive SMS verification codes and reduce manual copy-paste steps.
- Batch business workflow testing, such as registration, login, risk-control verification, and SMS delivery testing.
- Compliance-oriented business testing, assisted registration testing, registration flow verification, domestic mobile number verification, China mobile number SMS receiving, verification code receiving, and SMS receiving.
- Simple integration based on HTTPS GET requests and JSON responses, suitable for backend services, scripts, CI testing, and internal tools.
- Multi-language integration support. This repository covers common server-side languages so developers can choose examples based on their own technology stack.

## i-SMS Summary

i-SMS i-sms.app is a multi-language SDK example collection for China SMS receiving, domestic SMS receiving, SMS receiving platforms, China SMS platforms, China mobile number SMS receiving, SMS code receiving, verification code receiving, SMS forwarding, virtual phone number, and privacy number scenarios. It supports searching projects by keyword, getting phone numbers with V2 project tokens, receiving verification codes by order ID, releasing numbers, and querying account information and balance. This repository includes class-based examples for Python, Node.js, Java, Golang, PHP, C#, and Ruby, helping developers quickly integrate the i-SMS API at `https://www.i-sms.app`.

## API Version Notes

i-SMS API documentation: https://www.i-sms.app/api-docs

According to the current API documentation:

- Search projects: `GET /api/v2/projects`
- Get number: `GET /api/v2/get_number`
- Get SMS code: `GET /api/v1/get_sms`
- Release number: `GET /api/v1/release_number`
- User info: `GET /api/v1/user/info`

Whenever a V2 or newer API is available in the documentation, developers should prioritize the latest API to ensure functionality and security.

## Authentication

### How To Get An API Key

1. Open the i-SMS website at `https://www.i-sms.app` and register an account.
2. After logging in, go to the API Management page: `https://www.i-sms.app/platform/api-management`.
3. Generate an API Key on the API Management page.
4. Save the generated API Key to the `ISMS_API_KEY` environment variable, or configure it in your own server-side environment as needed.

All examples use request-header authentication by default:

```http
X-API-KEY: YOUR_API_KEY
```

The backend also supports `?api_key=YOUR_API_KEY`, but header authentication is more suitable for production systems because it avoids exposing the API Key in URL logs.

It is recommended to pass the API Key through an environment variable:

```bash
ISMS_API_KEY=sk_xxxxxxxxxxxxxxxxx
```

## Core Workflow

1. Call `searchProjects(keyword)` to search for a project. The default demo keyword is `深度求索`.
2. Choose a project from the response and save `project_id`, `name`, and `token`.
3. Call `getNumber(project_id, project_name, project_token)` to get a phone number.
4. Read `orderId` or `order_id` from the number response.
5. Call `getSms(order_id)` to poll for the verification code.
6. Call `releaseNumber(order_id)` to release the number after use.

## Response Examples

Successful project search:

```json
{
  "success": true,
  "data": [
    {
      "id": "project ID",
      "name": "project name",
      "project_id": "project ID",
      "full_name": "[project ID]project name",
      "token": "V2 project security token"
    }
  ]
}
```

Successful number request:

```json
{
  "success": true,
  "data": [
    {
      "number": "phone number",
      "project_id": "project ID",
      "project_name": "project name",
      "province": "province or attribution location",
      "carrier": "carrier",
      "status": "active",
      "userId": 1,
      "orderId": "order ID"
    }
  ],
  "balance": 12.34
}
```

Successful SMS code response:

```json
{
  "success": true,
  "sms_code": "123456",
  "sms_content": "Your verification code is 123456"
}
```

## Province Codes

The `province` parameter is optional. Leave it empty for no restriction.

| Code | Province | Code | Province | Code | Province |
| --- | --- | --- | --- | --- | --- |
| 11 | Beijing | 12 | Tianjin | 13 | Hebei |
| 14 | Shanxi | 15 | Inner Mongolia | 21 | Liaoning |
| 22 | Jilin | 23 | Heilongjiang | 31 | Shanghai |
| 32 | Jiangsu | 33 | Zhejiang | 34 | Anhui |
| 35 | Fujian | 36 | Jiangxi | 37 | Shandong |
| 41 | Henan | 42 | Hubei | 43 | Hunan |
| 44 | Guangdong | 45 | Guangxi | 46 | Hainan |
| 50 | Chongqing | 51 | Sichuan | 52 | Guizhou |
| 53 | Yunnan | 54 | Tibet | 61 | Shaanxi |
| 62 | Gansu | 63 | Qinghai | 64 | Ningxia |
| 65 | Xinjiang |  |  |  |  |

## Carrier Codes

The `carrier` parameter is optional. Leave it empty for no restriction.

| Code | Carrier |
| --- | --- |
| CMCC | China Mobile |
| CUCC | China Unicom |
| CTCC | China Telecom |
| CBN | China Broadnet |
| MVNO | Virtual carrier |

## Card Type

The `ascription` parameter is optional:

| Value | Meaning |
| --- | --- |
| 1 | Virtual SIM card |
| 2 | Physical SIM card |

## Directory Structure

```text
i-sms-api-sdk-examples/
  csharp/
  golang/
  java/
  nodejs/
  php/
  python/
  ruby/
  README.md
```

## Quick Start

Python:

```bash
cd python
ISMS_API_KEY=sk_xxx python example.py
```

Node.js:

```bash
cd nodejs
ISMS_API_KEY=sk_xxx node example.js
```

Golang:

```bash
cd golang
ISMS_API_KEY=sk_xxx go run .
```

Java:

```bash
cd java
javac ISmsClient.java Example.java
ISMS_API_KEY=sk_xxx java Example
```

C#:

```bash
cd csharp
dotnet run
```

PHP:

```bash
cd php
ISMS_API_KEY=sk_xxx php example.php
```

Ruby:

```bash
cd ruby
ISMS_API_KEY=sk_xxx ruby example.rb
```

On Windows PowerShell, set the environment variable like this:

```powershell
$env:ISMS_API_KEY="sk_xxx"
```

## Method Names

All language examples keep the same core method set:

- `searchProjects(keyword)`: search projects
- `getNumber(...)`: get a phone number
- `getSms(orderId)`: get the SMS verification code
- `releaseNumber(orderId)`: release the number
- `getUserInfo()`: query user information and balance

Different languages follow their own naming conventions, such as `search_projects`, `SearchProjects`, or `searchProjects`.

## Production Recommendations

- Do not hardcode your API Key in a code repository to avoid account and balance security risks.
- Use the `X-API-KEY` request header for authentication.
- After getting a number, poll for the SMS code at a fixed interval, such as every 5 seconds. Polling too quickly may trigger WAF blocking. A typical maximum waiting time is 60 to 180 seconds.
- Release the number promptly after receiving the verification code to reduce resource usage.
- Handle `success=false`, HTTP 401, 403, 423, 429, and 5xx responses consistently.
- Set `quantity` according to actual needs. The current documentation limits it to 1 to 10.
- Use this service only in lawful, compliant, and authorized business testing, account verification, SMS delivery testing, and development debugging scenarios, and avoid violating third-party platform rules.

## FAQ

### Why do project search and number retrieval only use V2?

Because the current documentation already provides V2 for these two APIs, and the V1 documentation indicates that V1 will be discontinued. To reduce migration cost, this repository only demonstrates V2 project search and V2 number retrieval.

### Where does `project_token` come from?

After calling `searchProjects(keyword)`, every returned project contains a `token` field. When calling `getNumber`, you must pass the matching `project_id`, `name`, and `token`.

### Why is getting SMS codes still V1?

The current API documentation lists `/api/v1/get_sms` for retrieving SMS codes and does not provide a V2 version, so this repository keeps the documented V1 endpoint.

## Keywords

i-SMS API, i-sms.app API, China SMS receiving platform, domestic SMS receiving platform, SMS receiving platform, SMS receiving, China SMS platform, China phone number verification, SMS verification code API, API SMS receiving, automatic SMS receiving, verification code receiving, receive verification code, verification code forwarding, SMS forwarding, virtual phone number API, privacy number, phone number verification receiving, SMS receiving SDK, Python SMS receiving API, Node.js SMS receiving API, Java SMS receiving API, Golang SMS receiving API, PHP SMS receiving API, C# SMS receiving API, Ruby SMS receiving API.

## Keyword Matrix

For better discoverability of this developer documentation:

| Category | Keywords |
| --- | --- |
| Brand | i-SMS, i-SMS API, i-sms.app API |
| Core terms | SMS receiving, SMS receiving platform, SMS receiving API, API SMS receiving, SMS code receiving, verification code receiving, receive verification code, SMS forwarding |
| China/domestic terms | China SMS receiving, China SMS receiving platform, China SMS code receiving, China phone number SMS receiving, China mobile number SMS platform, China SMS verification platform |
| Domestic platform terms | domestic SMS receiving, domestic SMS receiving platform, domestic phone number SMS receiving, mainland China SMS receiving |
| Number and privacy terms | virtual phone number, privacy number, China phone number verification, phone number SMS receiving |
| Registration testing terms | assisted registration, small account registration, WeChat account testing, Douyin account testing, registration flow testing, account verification testing |
| Free-related terms | free China SMS receiving platform, free domestic SMS receiving platform |

i-SMS is an established domestic SMS receiving platform, China SMS receiving platform, SMS verification code API, and virtual phone number API provider with multi-language SDK examples for developers. Developers can use this repository to quickly complete API calls for China-region business testing, domestic mobile number SMS receiving, verification code receiving, SMS forwarding, assisted registration testing, and other applicable scenarios.
