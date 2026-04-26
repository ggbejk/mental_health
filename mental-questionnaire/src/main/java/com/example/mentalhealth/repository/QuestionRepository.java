package com.example.mentalhealth.repository;

import com.example.mentalhealth.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
}
