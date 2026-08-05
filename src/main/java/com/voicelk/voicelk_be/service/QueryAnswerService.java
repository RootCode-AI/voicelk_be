package com.voicelk.voicelk_be.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.voicelk.voicelk_be.dto.QueryRequest;
import com.voicelk.voicelk_be.dto.QueryResponse;

@Service
public interface QueryAnswerService {

    /**
     * Submits a query: saves it to DB, calls Gemini to generate an answer,
     * saves the answer to DB, and returns both.
     */
    QueryResponse submitQuery(QueryRequest queryRequest);

    /**
     * Retrieves a query along with its answer by query ID.
     */
    QueryResponse getQueryWithAnswer(String queryId);

    /**
     * Retrieves all queries with answers for a given user, ordered by most recent.
     */
    List<QueryResponse> getQueryHistoryByUserId(String userId);
}
