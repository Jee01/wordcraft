package com.example.wordcraft.Service;

import com.example.wordcraft.DTO.VocaCreateRequestDTO;
import com.example.wordcraft.DTO.VocaResponseDTO;
import com.example.wordcraft.Entity.Users;
import com.example.wordcraft.Entity.VocaWords;
import com.example.wordcraft.Entity.Vocabularies;
import com.example.wordcraft.Repository.UserRepository;
import com.example.wordcraft.Repository.VocaWordsRepository;
import com.example.wordcraft.Repository.VocabulariesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VocaService {
    private final VocabulariesRepository vocabulariesRepository;
    private final VocaWordsRepository vocaWordsRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createVocabularies(VocaCreateRequestDTO vocaCreateRequestDTO, String email)
    {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("not found user"));

        Vocabularies vocabularies = new Vocabularies();

        vocabularies.setTitle(vocaCreateRequestDTO.getTitle());
        vocabularies.setTag(vocaCreateRequestDTO.getTag());
        vocabularies.set_public(vocaCreateRequestDTO.getIsPublic());
        vocabularies.setUser(user);

        Vocabularies saved = vocabulariesRepository.save(vocabularies);

        vocaCreateRequestDTO.getWords().forEach(wordDTO ->{
            VocaWords vocaWords = VocaWords.builder()
                    .vocabulary(saved)
                    .word(wordDTO.getWord())
                    .meanings(wordDTO.getMeaning())
                    .pos(wordDTO.getPos())
                    .ipa(wordDTO.getIpa())
                    .examples(wordDTO.getExamples())
                    .memoryTip(wordDTO.getMemoryTip())
                    .build();

            vocaWordsRepository.save(vocaWords);
        });
    }
}