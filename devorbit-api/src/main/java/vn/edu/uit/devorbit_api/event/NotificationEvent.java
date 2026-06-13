package vn.edu.uit.devorbit_api.event;

public record NotificationEvent(String type, String message, String targetUrl) {
}
