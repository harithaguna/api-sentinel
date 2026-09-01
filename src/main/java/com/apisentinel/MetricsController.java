package com.apisentinel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MetricsController {

    private final RequestRepository requestRepository;

    public MetricsController(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @GetMapping("/metrics")
    public ApiMetrics getMetrics() {

        List<ApiRequest> requests = requestRepository.findAll();

        ApiMetrics metrics = new ApiMetrics("All APIs");

        for (ApiRequest request : requests) {
            metrics.recordRequest(request);
        }

        return metrics;
    }

    @GetMapping("/metrics/apis")
    public List<ApiMetrics> getApiMetrics() {

        List<ApiRequest> requests = requestRepository.findAll();

        Map<String, ApiMetrics> metricsMap = new HashMap<>();

        for (ApiRequest request : requests) {

            String apiName = request.getApiName();

            ApiMetrics metrics = metricsMap.computeIfAbsent(
                    apiName,
                    ApiMetrics::new
            );

            metrics.recordRequest(request);
        }

        return metricsMap.values().stream().toList();
    }
}