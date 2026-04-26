const { ISmsClient } = require('./isms-client');

const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

async function main() {
  const client = new ISmsClient(process.env.ISMS_API_KEY);

  console.log('User info:');
  console.log(await client.getUserInfo());

  const projects = await client.searchProjects('深度求索');
  console.log('Project search result:');
  console.log(projects);

  if (!projects.success || !projects.data?.length) return;

  const project = projects.data[0];
  const numberResult = await client.getNumber({
    projectId: project.project_id,
    projectName: project.name,
    projectToken: project.token,
    quantity: 1,
  });
  console.log('Get number result:');
  console.log(numberResult);

  if (!numberResult.success || !numberResult.data?.length) return;

  const orderId = numberResult.data[0].orderId || numberResult.data[0].order_id;
  for (let i = 0; i < 12; i += 1) {
    const sms = await client.getSms({ orderId });
    console.log('SMS result:');
    console.log(sms);
    if (sms.success) break;
    await sleep(5000);
  }

  console.log('Release number:');
  console.log(await client.releaseNumber({ orderId }));
}

main().catch((error) => {
  console.error(error);
  process.exit(1);
});
