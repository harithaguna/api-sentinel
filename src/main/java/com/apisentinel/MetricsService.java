package com.apisentinel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    private final RequestRepository requestRepository;

    public MetricsService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public ApiMetrics getOverallMetrics() {

        List<ApiRequest> requests = requestRepository.findAll();

        ApiMetrics metrics = new ApiMetrics("All APIs");

        for (ApiRequest request : requests) {
            metrics.recordRequest(request);
        }

        return metrics;
    }

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