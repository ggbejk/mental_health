package com.example.mentalhealth.repository;

import com.example.mentalhealth.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
}