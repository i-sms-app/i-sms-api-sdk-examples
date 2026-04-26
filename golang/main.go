package main

import (
	"context"
	"fmt"
	"os"
	"time"
)

func main() {
	client, err := NewISmsClient(os.Getenv("ISMS_API_KEY"))
	if err != nil {
		panic(err)
	}

	ctx := context.Background()

	userInfo, _ := client.GetUserInfo(ctx)
	fmt.Println("User info:", userInfo)

	projects, err := client.SearchProjects(ctx, "深度求索")
	if err != nil {
		panic(err)
	}
	fmt.Println("Project search result:", projects)

	items, ok := projects["data"].([]any)
	if !ok || len(items) == 0 {
		return
	}
	project, ok := items[0].(map[string]any)
	if !ok {
		return
	}

	numberResult, err := client.GetNumber(ctx, GetNumberOptions{
		ProjectID:    fmt.Sprint(project["project_id"]),
		ProjectName:  fmt.Sprint(project["name"]),
		ProjectToken: fmt.Sprint(project["token"]),
		Quantity:     1,
	})
	if err != nil {
		panic(err)
	}
	fmt.Println("Get number result:", numberResult)

	numbers, ok := numberResult["data"].([]any)
	if !ok || len(numbers) == 0 {
		return
	}
	number, ok := numbers[0].(map[string]any)
	if !ok {
		return
	}
	orderID := valueString(number["orderId"])
	if orderID == "" {
		orderID = fmt.Sprint(number["order_id"])
	}
	if orderID == "" {
		return
	}

	for i := 0; i < 12; i++ {
		sms, err := client.GetSMS(ctx, orderID, "", "")
		if err != nil {
			panic(err)
		}
		fmt.Println("SMS result:", sms)
		if success, _ := sms["success"].(bool); success {
			break
		}
		time.Sleep(5 * time.Second)
	}

	released, _ := client.ReleaseNumber(ctx, orderID, "", "")
	fmt.Println("Release number:", released)
}

func valueString(value any) string {
	if value == nil {
		return ""
	}
	return fmt.Sprint(value)
}
