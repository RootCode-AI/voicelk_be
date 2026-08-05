package com.voicelk.voicelk_be.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.voicelk.voicelk_be.entity.Query;
import com.voicelk.voicelk_be.repository.QueryRepository;
import com.voicelk.voicelk_be.service.QueryService;

@Service
public class QueryServiceImpl implements QueryService {

    @Autowired
    private QueryRepository queryRepository;

    @Override
    public Query createQuery(Query query) {
        return queryRepository.save(query);
    }

    @Override
    public Optional<Query> getQueryById(String queryId) {
        return queryRepository.findById(queryId);
    }

    @Override
    public List<Query> getAllQueries() {
        return queryRepository.findAll();
    }

    @Override
    public List<Query> getQueriesByUserId(String userId) {
        return queryRepository.findByUserUserId(userId);
    }

    @Override
    public List<Query> getQueriesBySyllabusTopic(String syllabusTopic) {
        return queryRepository.findBySyllabusTopic(syllabusTopic);
    }

    @Override
    public List<Query> getQueriesByUserIdOrderByTimestamp(String userId) {
        return queryRepository.findByUserUserIdOrderByTimestampDesc(userId);
    }

    @Override
    public Query updateQuery(String queryId, Query query) {
        Query existingQuery = queryRepository.findById(queryId)
                .orElseThrow(() -> new RuntimeException("Query not found with id: " + queryId));

        existingQuery.setInputText(query.getInputText());
        existingQuery.setSyllabusTopic(query.getSyllabusTopic());

        return queryRepository.save(existingQuery);
    }

    @Override
    public void deleteQuery(String queryId) {
        if (!queryRepository.existsById(queryId)) {
            throw new RuntimeException("Query not found with id: " + queryId);
        }
        queryRepository.deleteById(queryId);
    }
}
