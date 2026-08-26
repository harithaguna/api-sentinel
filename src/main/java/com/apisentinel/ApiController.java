package com.apisentinel;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/")
    public String home() {
        return "API Sentinel is running!";
    }

    @GetMapping("/simulate")
    public SimulationResult simulate() {

        RequestRepository repository = new RequestRepository();

        return ApiSimulator.runSimulation(repository, 5);
    }
}