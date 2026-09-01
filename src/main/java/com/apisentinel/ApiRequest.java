package com.apisentinel;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Column;

@Entity
public class ApiRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String requestId;
    private String apiName;
    private int responseCode;
    private String status;
    private int attemptCount;

    @OneToMany(mappedBy = "apiRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApiAttempt> attempts;

    protected ApiRequest() {
        // Required by JPA
    }

    public ApiRequest(String requestId, String apiName) {
        this.requestId = requestId;
        this.apiName = apiName;
        this.attempts = new ArrayList<>();
    }

    public void addAttempt(ApiAttempt attempt) {
        attempts.add(attempt);
        attempt.setApiRequest(this);
    }

    public List<ApiAttempt> getAttempts() {
        return attempts;
    }

    public Long getId() {
        return id;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getApiName() {
        return apiName;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getStatus() {
        return status;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }
}