package com.apisentinel;

import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;   
public class ApiSimulator {
    private static final int[] RESPONSE_CODES = { 200, 200, 200, 400, 500, 502, 503, 504 };
    private static final String[] API_NAMES = {
            "Payment API",
            "User API",
            "Order API",
            "Notification API"
    };

    public static SimulationResult runSimulation(RequestRepository repository, int numberOfRequests) {
        Random random = new Random();

        for (int i = 1; i <= numberOfRequests; i++) {

            String requestId = "REQ-" + String.format("%04d", i);

            String apiName = API_NAMES[random.nextInt(API_NAMES.length)];

            ApiRequest request = new ApiRequest(requestId, apiName);

            processRequest(request);

            repository.save(request);

            printFinalResult(request);

            printAttemptHistory(request);

        }

        SimulationResult result = createSummary(repository.findAll());

printSummary(result);

printApiMetrics(repository.findAll());

        List<ApiRequest> dlqRequests = repository.findByStatus("DLQ");

        System.out.println();
        System.out.println("DLQ REQUESTS");
        System.out.println("----------------------------");

        for (ApiRequest request : dlqRequests) {

            System.out.println(
                    request.getRequestId()
                            + " | "
                            + request.getApiName()
                            + " | Attempts: "
                            + request.getAttemptCount());

        }
        return result;
    }

    private static void processRequest(ApiRequest request) {

        RetryPolicy policy = getRetryPolicy(request.getApiName());

        RetryService retryService = new RetryService(policy);

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

                attemptStatus = "SUCCESS";
                request.setStatus("SUCCESS");

                System.out.println("Status: SUCCESS");

                request.addAttempt(
                        new ApiAttempt(
                                attempt,
                                responseCode,
                                attemptStatus));

                break;

            } else if (!retryService.isRetryable(responseCode)) {

                attemptStatus = "FAILED";
                request.setStatus("FAILED");

                System.out.println("Status: PERMANENT FAILURE");

                request.addAttempt(
                        new ApiAttempt(
                                attempt,
                                responseCode,
                                attemptStatus));

                break;

            } else if (attempt > maxRetries) {

                attemptStatus = "DLQ";
                request.setStatus("DLQ");

                System.out.println("Maximum retries reached.");
                System.out.println("Status: MOVED TO DLQ");

                request.addAttempt(
                        new ApiAttempt(
                                attempt,
                                responseCode,
                                attemptStatus));

                break;

            } else {

                attemptStatus = "RETRY";
                request.setStatus("RETRYING");

                System.out.println("Status: RETRYABLE FAILURE");

                request.addAttempt(
                        new ApiAttempt(
                                attempt,
                                responseCode,
                                attemptStatus));

                int delaySeconds = retryService.getRetryDelaySeconds(attempt);

                System.out.println(
                        "Retrying in "
                                + delaySeconds
                                + " seconds...");

                waitBeforeRetry(delaySeconds);
            }
        }
    }

    private static int simulateApiCall() {
        Random random = new Random();
        return RESPONSE_CODES[random.nextInt(RESPONSE_CODES.length)];
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
                            attempt.getStatus());
        }
    }

    private static SimulationResult createSummary(List<ApiRequest> requests) {

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

        double averageAttempts = totalRequests == 0
                ? 0
                : totalAttempts * 1.0 / totalRequests;

        double successRate = totalRequests == 0
                ? 0
                : (success * 100.0) / totalRequests;

        return new SimulationResult(
                totalRequests,
                success,
                failure,
                dlq,
                successRate,
                totalRetries,
                averageAttempts);
    }

    private static void printSummary(SimulationResult result) {

        System.out.println();
        System.out.println("================================");
        System.out.println("API SENTINEL SUMMARY");
        System.out.println("================================");

        System.out.println(
                "Total Requests: " + result.getTotalRequests());

        System.out.println(
                "Successful: " + result.getSuccessful());

        System.out.println(
                "Failed: " + result.getFailed());

        System.out.println(
                "DLQ: " + result.getDlq());

        System.out.println(
                "SUCCESS RATE: "
                        + String.format("%.2f", result.getSuccessRate())
                        + "%");

        System.out.println(
                "Total Retries: " + result.getTotalRetries());

        System.out.println(
                "Average Attempts: "
                        + result.getAverageAttempts());
    }

private static void printApiMetrics(List<ApiRequest> requests) {

    Map<String, ApiMetrics> metricsMap = new HashMap<>();

    for (ApiRequest request : requests) {

        String apiName = request.getApiName();

        ApiMetrics metrics =
                metricsMap.computeIfAbsent(
                        apiName,
                        ApiMetrics::new);

        metrics.recordRequest(request);
    }

    System.out.println();
    System.out.println("================================");
    System.out.println("API PERFORMANCE");
    System.out.println("================================");

    for (ApiMetrics metrics : metricsMap.values()) {

        System.out.println();
        System.out.println(metrics.getApiName());
        System.out.println("----------------------------");

        System.out.println(
                "Requests: "
                        + metrics.getTotalRequests());

        System.out.println(
                "Successful: "
                        + metrics.getSuccessful());

        System.out.println(
                "Failed: "
                        + metrics.getFailed());

        System.out.println(
                "DLQ: "
                        + metrics.getDlq());

        System.out.println(
                "Total Retries: "
                        + metrics.getTotalRetries());

        System.out.println(
                "Success Rate: "
                        + String.format(
                                "%.2f",
                                metrics.getSuccessRate())
                        + "%");
    }

}
}