package com.example.wordcraft.Repository;

import com.example.wordcraft.Entity.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult,String> {
}
