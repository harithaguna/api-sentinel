package com.apisentinel;

import java.util.Random;

public class RetryService {

    private final RetryPolicy retryPolicy;
    private final Random random = new Random();

    public RetryService(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
    }

    public boolean isRetryable(int statusCode) {

        return statusCode == 408 ||
               statusCode == 429 ||
               statusCode == 500 ||
               statusCode == 502 ||
               statusCode == 503 ||
               statusCode == 504;
    }

    public boolean shouldRetry(int statusCode, int retryAttempt) {

        return isRetryable(statusCode) &&
               retryAttempt <= retryPolicy.getMaxRetries();
    }

    public int getRetryDelaySeconds(int retryAttempt) {

        int exponentialDelay =
                retryPolicy.getInitialDelaySeconds()
                        * (int) Math.pow(2, retryAttempt - 1);

        int jitter = random.nextInt(2);

        return exponentialDelay + jitter;
    }
}