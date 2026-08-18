package com.apisentinel;

import java.util.*;

public class ApiRequest {
    private String requestId;
    private String apiName;
    private int responseCode;
    private String status;
    private int attemptCount;
    private List<ApiAttempt> attempts;

    public ApiRequest(String requestId, String apiName) {
        this.requestId = requestId;
        this.apiName = apiName;
        this.attempts = new ArrayList<>();
    }

    public void addAttempt(ApiAttempt attempt) {
        attempts.add(attempt);
    }

    public List<ApiAttempt> getAttempts() {
        return attempts;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getApiName() {
        return apiName;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getStatus() {
        return status;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }
}