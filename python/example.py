import os
import time

from isms_client import ISmsClient


def main():
    api_key = os.environ.get("ISMS_API_KEY")
    client = ISmsClient(api_key)

    print("User info:")
    print(client.get_user_info())

    projects = client.search_projects("深度求索")
    print("Project search result:")
    print(projects)

    if not projects.get("success") or not projects.get("data"):
        return

    project = projects["data"][0]
    number_result = client.get_number(
        project_id=project["project_id"],
        project_name=project["name"],
        project_token=project["token"],
        quantity=1,
    )
    print("Get number result:")
    print(number_result)

    if not number_result.get("success") or not number_result.get("data"):
        return

    order_id = number_result["data"][0].get("orderId") or number_result["data"][0].get("order_id")
    for _ in range(12):
        sms = client.get_sms(order_id=order_id)
        print("SMS result:")
        print(sms)
        if sms.get("success"):
            break
        time.sleep(5)

    print("Release number:")
    print(client.release_number(order_id=order_id))


if __name__ == "__main__":
    main()
