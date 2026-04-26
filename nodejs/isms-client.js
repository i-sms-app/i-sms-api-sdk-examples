class ISmsClient {
  constructor(apiKey, options = {}) {
    if (!apiKey) throw new Error('apiKey is required');
    this.apiKey = apiKey;
    this.baseUrl = (options.baseUrl || 'https://www.i-sms.app').replace(/\/+$/, '');
    this.timeoutMs = options.timeoutMs || 20000;
  }

  searchProjects(keyword) {
    return this.get('/api/v2/projects', { keyword });
  }

  getNumber({
    projectId,
    projectName,
    projectToken,
    quantity = 1,
    phone,
    province,
    carrier,
    ascription,
  }) {
    return this.get('/api/v2/get_number', {
      project_id: projectId,
      project_name: projectName,
      project_token: projectToken,
      quantity,
      phone,
      province,
      carrier,
      ascription,
    });
  }

  getSms({ orderId, phoneNumber, projectId } = {}) {
    return this.get('/api/v1/get_sms', {
      order_id: orderId,
      phone_number: phoneNumber,
      project_id: projectId,
    });
  }

  releaseNumber({ orderId, phoneNumber, projectId } = {}) {
    return this.get('/api/v1/release_number', {
      order_id: orderId,
      phone_number: phoneNumber,
      project_id: projectId,
    });
  }

  getUserInfo() {
    return this.get('/api/v1/user/info');
  }

  async get(path, params = {}) {
    const url = new URL(`${this.baseUrl}${path}`);
    for (const [key, value] of Object.entries(params)) {
      if (value !== undefined && value !== null && value !== '') {
        url.searchParams.set(key, String(value));
      }
    }

    const controller = new AbortController();
    const timer = setTimeout(() => controller.abort(), this.timeoutMs);

    try {
      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Accept': 'application/json',
          'X-API-KEY': this.apiKey,
        },
        signal: controller.signal,
      });
      const data = await response.json().catch(() => ({
        success: false,
        message: 'Invalid JSON response',
      }));
      if (!response.ok) data.http_status = response.status;
      return data;
    } finally {
      clearTimeout(timer);
    }
  }
}

module.exports = { ISmsClient };
