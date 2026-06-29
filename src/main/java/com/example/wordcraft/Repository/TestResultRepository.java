package com.example.wordcraft.Repository;

import com.example.wordcraft.Entity.Test.TestResult;
import com.example.wordcraft.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long> {
    List<TestResult> findTop10ByUserOrderByTakenAtDesc(Users user);
}
