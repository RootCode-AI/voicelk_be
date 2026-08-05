package com.voicelk.voicelk_be.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.voicelk.voicelk_be.entity.Query;

@Service
public interface QueryService {

    Query createQuery(Query query);

    Optional<Query> getQueryById(String queryId);

    List<Query> getAllQueries();

    List<Query> getQueriesByUserId(String userId);

    List<Query> getQueriesBySyllabusTopic(String syllabusTopic);

    List<Query> getQueriesByUserIdOrderByTimestamp(String userId);

    Query updateQuery(String queryId, Query query);

    void deleteQuery(String queryId);
}
