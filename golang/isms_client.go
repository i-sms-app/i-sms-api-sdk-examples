package main

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"net/http"
	"net/url"
	"strings"
	"time"
)

type ISmsClient struct {
	APIKey     string
	BaseURL    string
	HTTPClient *http.Client
}

type GetNumberOptions struct {
	ProjectID    string
	ProjectName  string
	ProjectToken string
	Quantity     int
	Phone        string
	Province     string
	Carrier      string
	Ascription   string
}

func NewISmsClient(apiKey string) (*ISmsClient, error) {
	if strings.TrimSpace(apiKey) == "" {
		return nil, errors.New("apiKey is required")
	}
	return &ISmsClient{
		APIKey:  apiKey,
		BaseURL: "https://www.i-sms.app",
		HTTPClient: &http.Client{
			Timeout: 20 * time.Second,
		},
	}, nil
}

func (c *ISmsClient) SearchProjects(ctx context.Context, keyword string) (map[string]any, error) {
	return c.get(ctx, "/api/v2/projects", map[string]string{"keyword": keyword})
}

func (c *ISmsClient) GetNumber(ctx context.Context, opts GetNumberOptions) (map[string]any, error) {
	quantity := opts.Quantity
	if quantity == 0 {
		quantity = 1
	}
	return c.get(ctx, "/api/v2/get_number", map[string]string{
		"project_id":    opts.ProjectID,
		"project_name":  opts.ProjectName,
		"project_token": opts.ProjectToken,
		"quantity":      fmt.Sprintf("%d", quantity),
		"phone":         opts.Phone,
		"province":      opts.Province,
		"carrier":       opts.Carrier,
		"ascription":    opts.Ascription,
	})
}

func (c *ISmsClient) GetSMS(ctx context.Context, orderID string, phoneNumber string, projectID string) (map[string]any, error) {
	return c.get(ctx, "/api/v1/get_sms", map[string]string{
		"order_id":     orderID,
		"phone_number": phoneNumber,
		"project_id":   projectID,
	})
}

func (c *ISmsClient) ReleaseNumber(ctx context.Context, orderID string, phoneNumber string, projectID string) (map[string]any, error) {
	return c.get(ctx, "/api/v1/release_number", map[string]string{
		"order_id":     orderID,
		"phone_number": phoneNumber,
		"project_id":   projectID,
	})
}

func (c *ISmsClient) GetUserInfo(ctx context.Context) (map[string]any, error) {
	return c.get(ctx, "/api/v1/user/info", nil)
}

func (c *ISmsClient) get(ctx context.Context, path string, params map[string]string) (map[string]any, error) {
	endpoint, err := url.Parse(strings.TrimRight(c.BaseURL, "/") + path)
	if err != nil {
		return nil, err
	}

	query := endpoint.Query()
	for key, value := range params {
		if value != "" {
			query.Set(key, value)
		}
	}
	endpoint.RawQuery = query.Encode()

	req, err := http.NewRequestWithContext(ctx, http.MethodGet, endpoint.String(), nil)
	if err != nil {
		return nil, err
	}
	req.Header.Set("Accept", "application/json")
	req.Header.Set("X-API-KEY", c.APIKey)

	resp, err := c.HTTPClient.Do(req)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	var data map[string]any
	if err := json.NewDecoder(resp.Body).Decode(&data); err != nil {
		return nil, err
	}
	if resp.StatusCode >= 400 {
		data["http_status"] = resp.StatusCode
	}
	return data, nil
}
