package com.example.wordcraft.Service;

import com.example.wordcraft.Entity.Vocabularies;
import com.example.wordcraft.Repository.VocaWordsRepository;
import com.example.wordcraft.Repository.VocabulariesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VocaService {
    private final VocabulariesRepository vocabulariesRepository;
    private final VocaWordsRepository vocaWordsRepository;

    //이후 변경
    public Vocabularies createVocabularies(Vocabularies vocabularies) {
        return null;
    }
}
