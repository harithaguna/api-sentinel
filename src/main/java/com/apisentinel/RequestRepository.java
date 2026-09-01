package com.apisentinel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository extends JpaRepository<ApiRequest, Long> {

    ApiRequest findByRequestId(String requestId);

    List<ApiRequest> findByStatus(String status);
}