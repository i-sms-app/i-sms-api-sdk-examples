require 'json'
require 'net/http'
require 'uri'

class ISmsClient
  def initialize(api_key, base_url: 'https://www.i-sms.app', timeout: 20)
    raise ArgumentError, 'api_key is required' if api_key.nil? || api_key.empty?

    @api_key = api_key
    @base_url = base_url.sub(%r{/+\z}, '')
    @timeout = timeout
  end

  def search_projects(keyword)
    get('/api/v2/projects', keyword: keyword)
  end

  def get_number(project_id:, project_name:, project_token:, quantity: 1, phone: nil, province: nil, carrier: nil, ascription: nil)
    get('/api/v2/get_number', {
      project_id: project_id,
      project_name: project_name,
      project_token: project_token,
      quantity: quantity,
      phone: phone,
      province: province,
      carrier: carrier,
      ascription: ascription
    })
  end

  def get_sms(order_id: nil, phone_number: nil, project_id: nil)
    get('/api/v1/get_sms', order_id: order_id, phone_number: phone_number, project_id: project_id)
  end

  def release_number(order_id: nil, phone_number: nil, project_id: nil)
    get('/api/v1/release_number', order_id: order_id, phone_number: phone_number, project_id: project_id)
  end

  def get_user_info
    get('/api/v1/user/info')
  end

  private

  def get(path, params = {})
    uri = URI("#{@base_url}#{path}")
    clean_params = params.reject { |_key, value| value.nil? || value == '' }
    uri.query = URI.encode_www_form(clean_params) unless clean_params.empty?

    request = Net::HTTP::Get.new(uri)
    request['Accept'] = 'application/json'
    request['X-API-KEY'] = @api_key

    response = Net::HTTP.start(uri.hostname, uri.port, use_ssl: uri.scheme == 'https', open_timeout: @timeout, read_timeout: @timeout) do |http|
      http.request(request)
    end

    data = JSON.parse(response.body)
    data['http_status'] = response.code.to_i if response.code.to_i >= 400
    data
  rescue JSON::ParserError
    { 'success' => false, 'message' => response&.body || 'Invalid JSON response' }
  end
end
