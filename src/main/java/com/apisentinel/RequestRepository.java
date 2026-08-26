package com.apisentinel;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RequestRepository {

    private List<ApiRequest> requests;

    public RequestRepository() {
        requests = new ArrayList<>();
    }

    public void save(ApiRequest request) {
        requests.add(request);
    }

    public List<ApiRequest> findAll() {
        return requests;
    }

    public ApiRequest findById(String requestId) {

        for (ApiRequest request : requests) {

            if (request.getRequestId().equals(requestId)) {
                return request;
            }
        }

        return null;
    }
    public List<ApiRequest> findByStatus(String status) {

    List<ApiRequest> results = new ArrayList<>();

    for (ApiRequest request : requests) {

        if (status.equals(request.getStatus())) {
            results.add(request);
        }
    }

    return results;
}
}