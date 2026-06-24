package com.example.wordcraft.Service;

import com.example.wordcraft.Entity.Users;
import com.example.wordcraft.Entity.VocaWords;
import com.example.wordcraft.Entity.Vocabularies;
import com.example.wordcraft.Repository.UserRepository;
import com.example.wordcraft.Repository.VocaWordsRepository;
import com.example.wordcraft.Repository.VocabulariesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityService {
    private final VocabulariesRepository vocabulariesRepository;
    private final VocaWordsRepository vocaWordsRepository;
    private final UserRepository userRepository;

    public void copyVocabularies(Long id, String email){
        Users requestUser = userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("not found user"));

        Vocabularies orginVocabularies = vocabulariesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("vocabularies not found"));
        List<VocaWords> originVocaWords = vocaWordsRepository.findByVocabularyId(orginVocabularies.getId());

        Vocabularies copyVocabularies = Vocabularies.builder()
                .originId(orginVocabularies.getId())
                .title(orginVocabularies.getTitle())
                .tag(orginVocabularies.getTag())
                .isPublic(orginVocabularies.getIsPublic())
                .user(requestUser)
                .build();

        Vocabularies saved = vocabulariesRepository.save(copyVocabularies);

        List<VocaWords> copyWordsList = originVocaWords.stream()
                .map(copyWords -> VocaWords.builder()
                        .vocabulary(saved)
                        .word(copyWords.getWord())
                        .meanings(copyWords.getMeanings())
                        .pos(copyWords.getPos())
                        .ipa(copyWords.getIpa())
                        .examples(copyWords.getExamples())
                        .memoryTip(copyWords.getMemoryTip())
                        .build())
                .toList();

        vocaWordsRepository.saveAll(copyWordsList);
    }
}
