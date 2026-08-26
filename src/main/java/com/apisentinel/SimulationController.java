package com.apisentinel;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SimulationController {

    private final RequestRepository requestRepository;

    public SimulationController(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @PostMapping("/simulate")
    public String simulate() {

        ApiSimulator.runSimulation(requestRepository, 5);

        return "Simulation completed successfully";
    }
}