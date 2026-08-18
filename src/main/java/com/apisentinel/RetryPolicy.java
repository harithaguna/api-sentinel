package com.apisentinel;

public class RetryPolicy {

    private int maxRetries;
    private int initialDelaySeconds;

    public RetryPolicy(int maxRetries, int initialDelaySeconds) {
        this.maxRetries = maxRetries;
        this.initialDelaySeconds = initialDelaySeconds;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public int getInitialDelaySeconds() {
        return initialDelaySeconds;
    }
}