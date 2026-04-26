require_relative './isms_client'

client = ISmsClient.new(ENV.fetch('ISMS_API_KEY'))

puts 'User info:'
p client.get_user_info

projects = client.search_projects('深度求索')
puts 'Project search result:'
p projects

exit unless projects['success'] && projects['data'] && !projects['data'].empty?

project = projects['data'][0]
number_result = client.get_number(
  project_id: project['project_id'],
  project_name: project['name'],
  project_token: project['token'],
  quantity: 1
)
puts 'Get number result:'
p number_result

exit unless number_result['success'] && number_result['data'] && !number_result['data'].empty?

order_id = number_result['data'][0]['orderId'] || number_result['data'][0]['order_id']
12.times do
  sms = client.get_sms(order_id: order_id)
  puts 'SMS result:'
  p sms
  break if sms['success']

  sleep 5
end

puts 'Release number:'
p client.release_number(order_id: order_id)
