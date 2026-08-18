package com.apisentinel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ApiSimulator {
    private static final int MAX_RETRIES = 3;
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
        }
        printSummary(requests);
    }

    private static void processRequest(ApiRequest request) {

        int attempt = 0;
        while (attempt <= MAX_RETRIES) {
            attempt++;
            int responseCode = simulateApiCall();
            request.setResponseCode(responseCode);
            request.setAttemptCount(attempt);
            printAttempt(request);

            if (responseCode == 200) {
                request.setStatus("SUCCESS");
                System.out.println("Status: SUCCESS");
                break;
            } else if (shouldRetry(responseCode)) {
                request.setStatus("RETRYING");
                System.out.println("Status: RETRYABLE FAILURE");
                if (attempt > MAX_RETRIES) {
                    request.setStatus("DLQ");
                    System.out.println("Maximum retries reached.");
                    System.out.println("Status: MOVED TO DLQ");
                    break;
                } else {
                    int delaySeconds = calculateDelay(attempt);
                    System.out.println("Retrying in " + delaySeconds + " seconds...");
                    waitBeforeRetry(delaySeconds);
                }
            } else {
                request.setStatus("FAILED");
                System.out.println("Status: PERMANENT FAILURE");
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

    private static int calculateDelay(int attempt) {

        return (int) Math.pow(2, attempt - 1);
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
