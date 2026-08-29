package com.voicelk.voicelk_be.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voicelk.voicelk_be.dto.QueryDto;
import com.voicelk.voicelk_be.entity.Query;
import com.voicelk.voicelk_be.mapper.EntityMapper;
import com.voicelk.voicelk_be.service.QueryService;

@RestController
@RequestMapping("/api/queries")
@CrossOrigin(origins = "*")
public class QueryController {

    @Autowired
    private QueryService queryService;

    @Autowired
    private EntityMapper entityMapper;

    @PostMapping
    public ResponseEntity<QueryDto> createQuery(@RequestBody Query query) {
        Query createdQuery = queryService.createQuery(query);
        return new ResponseEntity<>(entityMapper.toDto(createdQuery), HttpStatus.CREATED);
    }

    @GetMapping("/{queryId}")
    public ResponseEntity<QueryDto> getQueryById(@PathVariable String queryId) {
        return queryService.getQueryById(queryId)
                .map(entityMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<QueryDto>> getAllQueries() {
        return ResponseEntity.ok(entityMapper.toQueryDtoList(queryService.getAllQueries()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<QueryDto>> getQueriesByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(entityMapper.toQueryDtoList(queryService.getQueriesByUserId(userId)));
    }

    @GetMapping("/topic/{syllabusTopic}")
    public ResponseEntity<List<QueryDto>> getQueriesBySyllabusTopic(@PathVariable String syllabusTopic) {
        return ResponseEntity.ok(entityMapper.toQueryDtoList(queryService.getQueriesBySyllabusTopic(syllabusTopic)));
    }

    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<List<QueryDto>> getQueriesByUserIdOrderByTimestamp(@PathVariable String userId) {
        return ResponseEntity
                .ok(entityMapper.toQueryDtoList(queryService.getQueriesByUserIdOrderByTimestamp(userId)));
    }

    @PutMapping("/{queryId}")
    public ResponseEntity<QueryDto> updateQuery(@PathVariable String queryId, @RequestBody Query query) {
        return ResponseEntity.ok(entityMapper.toDto(queryService.updateQuery(queryId, query)));
    }

    @DeleteMapping("/{queryId}")
    public ResponseEntity<Void> deleteQuery(@PathVariable String queryId) {
        queryService.deleteQuery(queryId);
        return ResponseEntity.noContent().build();
    }
}
