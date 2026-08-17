package com.apisentinel;

import java.util.Random;

public class ApiSimulator {
    private static final int MAX_RETRIES = 3;
    private static final int[] RESPONSE_CODES = {200, 200, 200,400,500, 502, 503, 504};
    public static void main(String[] args) {
        ApiRequest request = new ApiRequest("REQ-1001", "Payment API");
        processRequest(request);
        printFinalResult(request);
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
}