package com.example.wordcraft.Service.Test;

import com.example.wordcraft.Repository.TestResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestService {
    private final TestResultRepository testResultRepository;
}
