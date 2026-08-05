package com.voicelk.voicelk_be.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.voicelk.voicelk_be.dto.QueryRequest;
import com.voicelk.voicelk_be.dto.QueryResponse;

@Service
public interface QueryAnswerService {

    QueryResponse submitQuery(QueryRequest queryRequest);

    QueryResponse getQueryWithAnswer(String queryId);

    List<QueryResponse> getQueryHistoryByUserId(String userId);
}
