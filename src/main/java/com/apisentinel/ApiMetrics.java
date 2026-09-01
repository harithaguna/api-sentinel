package com.apisentinel;

public class ApiMetrics {

    private final String apiName;

    private int totalRequests;
    private int successful;
    private int failed;
    private int dlq;
    private int totalRetries;

    public ApiMetrics(String apiName) {
        this.apiName = apiName;
    }

    public void recordRequest(ApiRequest request) {

        totalRequests++;

        totalRetries += request.getAttemptCount() - 1;

        if ("SUCCESS".equals(request.getStatus())) {
            successful++;

        } else if ("DLQ".equals(request.getStatus())) {
            dlq++;

        } else {
            failed++;
        }
    }

    public double getSuccessRate() {

        if (totalRequests == 0) {
            return 0;
        }

        return Math.round((successful * 100.0 / totalRequests) * 100.0) / 100.0;
    }

    public String getApiName() {
        return apiName;
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public int getSuccessful() {
        return successful;
    }

    public int getFailed() {
        return failed;
    }

    public int getDlq() {
        return dlq;
    }

    public int getTotalRetries() {
        return totalRetries;
    }
}