package com.voicelk.voicelk_be.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.voicelk.voicelk_be.entity.Answer;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, String> {

    Optional<Answer> findByQueryQueryId(String queryId);
}
