package com.voicelk.voicelk_be.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voicelk.voicelk_be.entity.Query;

@Repository
public interface QueryRepository extends JpaRepository<Query, String> {

    List<Query> findByUserUserId(String userId);

    List<Query> findBySyllabusTopic(String syllabusTopic);

    List<Query> findByUserUserIdOrderByTimestampDesc(String userId);
}
