package com.apisentinel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ApiSimulator {
    private static final int[] RESPONSE_CODES = { 200, 200, 200, 400, 500, 502, 503, 504 };
    private static final String[] API_NAMES = {
            "Payment API",
            "User API",
            "Order API",
            "Notification API"
    };

    public static void main(String[] args) {
        List<ApiRequest> requests = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String reqId = "REQ-000" + i;
            Random random = new Random();
            String apiName=API_NAMES[random.nextInt(API_NAMES.length)];
            ApiRequest request = new ApiRequest(reqId,apiName);
            processRequest(request);
            requests.add(request);
            printFinalResult(request);
            printAttemptHistory(request);
        }
        printSummary(requests);
    }

    private static void processRequest(ApiRequest request) {

    RetryPolicy policy =
            getRetryPolicy(request.getApiName());

    int maxRetries = policy.getMaxRetries();

    int attempt = 0;

    while (attempt <= maxRetries) {

        // 1. Make API attempt
        attempt++;

        int responseCode = simulateApiCall();

        request.setResponseCode(responseCode);
        request.setAttemptCount(attempt);

        printAttempt(request);


        // 2. Handle the response
        String attemptStatus;

        if (responseCode == 200) {

            // SUCCESS
            attemptStatus = "SUCCESS";

            request.setStatus("SUCCESS");

            System.out.println("Status: SUCCESS");

            request.addAttempt(
                    new ApiAttempt(
                            attempt,
                            responseCode,
                            attemptStatus
                    )
            );

            break;

        } else if (shouldRetry(responseCode)) {

            // RETRYABLE FAILURE
            attemptStatus = "RETRY";

            request.setStatus("RETRYING");

            System.out.println("Status: RETRYABLE FAILURE");

            if (attempt > maxRetries) {

                attemptStatus = "DLQ";

                request.setStatus("DLQ");

                System.out.println("Maximum retries reached.");
                System.out.println("Status: MOVED TO DLQ");

                request.addAttempt(
                        new ApiAttempt(
                                attempt,
                                responseCode,
                                attemptStatus
                        )
                );

                break;

            } else {

                request.addAttempt(
                        new ApiAttempt(
                                attempt,
                                responseCode,
                                attemptStatus
                        )
                );

                int delaySeconds =
                        policy.getInitialDelaySeconds()
                        * (int) Math.pow(2, attempt - 1);

                System.out.println(
                        "Retrying in "
                                + delaySeconds
                                + " seconds..."
                );

                waitBeforeRetry(delaySeconds);
            }

        } else {

            // PERMANENT FAILURE
            attemptStatus = "FAILED";

            request.setStatus("FAILED");

            System.out.println("Status: PERMANENT FAILURE");

            request.addAttempt(
                    new ApiAttempt(
                            attempt,
                            responseCode,
                            attemptStatus
                    )
            );

            break;
        }
    }
}

    private static int simulateApiCall() {
        Random random = new Random();
        return RESPONSE_CODES[random.nextInt(RESPONSE_CODES.length)];
    }

    private static boolean shouldRetry(int responseCode) {
        return responseCode == 408 ||
                responseCode == 429 ||
                responseCode == 500 ||
                responseCode == 502 ||
                responseCode == 503 ||
                responseCode == 504;
    }

    private static void waitBeforeRetry(int delaySeconds) {

        try {

            Thread.sleep(delaySeconds * 1000L);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            System.out.println("Retry interrupted.");
        }
    }

    private static void printAttempt(ApiRequest request) {

        System.out.println();
        System.out.println("----------------------------");

        System.out.println(
                "Request ID: " + request.getRequestId());

        System.out.println(
                "API: " + request.getApiName());

        System.out.println(
                "Attempt: " + request.getAttemptCount());

        System.out.println(
                "Response Code: " + request.getResponseCode());
    }
    private static RetryPolicy getRetryPolicy(String apiName) {

    switch (apiName) {

        case "Payment API":
            return new RetryPolicy(3, 1);

        case "User API":
            return new RetryPolicy(2, 1);

        case "Order API":
            return new RetryPolicy(3, 2);

        case "Notification API":
            return new RetryPolicy(5, 1);

        default:
            return new RetryPolicy(3, 1);
    }
}
    private static void printFinalResult(ApiRequest request) {

        System.out.println();
        System.out.println("============================");
        System.out.println("FINAL RESULT");
        System.out.println("============================");

        System.out.println(
                "Request ID: " + request.getRequestId());

        System.out.println(
                "Final Status: " + request.getStatus());

        System.out.println(
                "Total Attempts: " + request.getAttemptCount());
    }
    private static void printAttemptHistory(ApiRequest request) {

    System.out.println();
    System.out.println("ATTEMPT HISTORY");
    System.out.println("----------------------------");

    for (ApiAttempt attempt : request.getAttempts()) {

       System.out.println(
        "Attempt " +
        attempt.getAttemptNumber() +
        " | Time: " +
        attempt.getTimestamp() +
        " | Response: " +
        attempt.getResponseCode() +
        " | Status: " +
        attempt.getStatus()
);
    }
}
    private static void printSummary(List<ApiRequest> requests) {
        int success = 0;
        int failure = 0;
        int dlq = 0;
        int totalAttempts = 0;
        for (ApiRequest request : requests) {
            totalAttempts += request.getAttemptCount();
            if ("SUCCESS".equals(request.getStatus())) {
                success++;
            } else if ("DLQ".equals(request.getStatus())) {
                dlq++;
            } else {
                failure++;
            }
        }
        int totalRequests = requests.size();
        int totalRetries = totalAttempts - totalRequests;
        double averageAttempts = totalAttempts * 1.0 / totalRequests;
        double successRate = (success * 100.0) / totalRequests;
        System.out.println();
        System.out.println("================================");
        System.out.println("API SENTINEL SUMMARY");
        System.out.println("================================");
        System.out.println("Total Requests: " + totalRequests);
        System.out.println("Successful: " + success);
        System.out.println("Failed: " + failure);
        System.out.println("DLQ: " + dlq);
        System.out.println("SUCESS RATE: " + String.format("%.2f", successRate) + "%");
        System.out.println("Total Retries: " + totalRetries);
        System.out.println("Average Attempts: " + averageAttempts);

    }
}
