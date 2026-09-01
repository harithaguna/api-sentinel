package com.apisentinel;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api")
public class RequestController {

    private final RequestRepository requestRepository;

    public RequestController(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @GetMapping("/requests")
    public List<ApiRequest> getAllRequests() {
        return requestRepository.findAll();
    }

    @GetMapping("/requests/status/{status}")
    public List<ApiRequest> getRequestsByStatus(@PathVariable String status) {
        return requestRepository.findByStatus(status);
    }
}