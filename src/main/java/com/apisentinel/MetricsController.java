package com.apisentinel;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MetricsController {

    private final MetricsService metricsService;

    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping("/metrics")
    public ApiMetrics getMetrics() {
        return metricsService.getOverallMetrics();
    }

    @GetMapping("/metrics/apis")
    public List<ApiMetrics> getApiMetrics() {
        return metricsService.getApiMetrics();
    }
}