package com.example.wordcraft.Service;

import com.example.wordcraft.Entity.Vocabularies;
import com.example.wordcraft.Repository.VocabulariesRepository;
import org.springframework.stereotype.Service;

@Service
public class CommunityService {
    private final VocabulariesRepository vocabulariesRepository;

    public CommunityService(VocabulariesRepository vocabulariesRepository) {
        this.vocabulariesRepository = vocabulariesRepository;
    }
}
