package com.apisentinel;
import java.time.LocalDateTime;
public class ApiAttempt {

    private int attemptNumber;
    private int responseCode;
    private String status;
    private LocalDateTime timestamp;
    public ApiAttempt(int attemptNumber, int responseCode, String status) {
    this.attemptNumber = attemptNumber;
    this.responseCode = responseCode;
    this.status = status;
    this.timestamp = LocalDateTime.now();
}
    public LocalDateTime getTimestamp() {
    return timestamp;
}
    public int getAttemptNumber() {
        return attemptNumber;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getStatus() {
        return status;
    }
}