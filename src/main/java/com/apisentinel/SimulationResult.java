package com.apisentinel;

public class SimulationResult {

    private int totalRequests;
    private int successful;
    private int failed;
    private int dlq;
    private double successRate;
    private int totalRetries;
    private double averageAttempts;

    public SimulationResult(
            int totalRequests,
            int successful,
            int failed,
            int dlq,
            double successRate,
            int totalRetries,
            double averageAttempts) {

        this.totalRequests = totalRequests;
        this.successful = successful;
        this.failed = failed;
        this.dlq = dlq;
        this.successRate = successRate;
        this.totalRetries = totalRetries;
        this.averageAttempts = averageAttempts;
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

    public double getSuccessRate() {
        return successRate;
    }

    public int getTotalRetries() {
        return totalRetries;
    }

    public double getAverageAttempts() {
        return averageAttempts;
    }
}