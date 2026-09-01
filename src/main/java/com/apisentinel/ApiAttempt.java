package com.apisentinel;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ApiAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int attemptNumber;
    private int responseCode;
    private String status;
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "api_request_id")
    private ApiRequest apiRequest;

    protected ApiAttempt() {
        // Required by JPA
    }

    public ApiAttempt(int attemptNumber, int responseCode, String status) {
        this.attemptNumber = attemptNumber;
        this.responseCode = responseCode;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getAttemptNumber() {
        return attemptNumber;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getStatus() {
        return status;
    }

    public void setApiRequest(ApiRequest apiRequest) {
    this.apiRequest = apiRequest;
}
}