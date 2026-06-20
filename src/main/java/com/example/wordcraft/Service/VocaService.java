package com.example.wordcraft.Service;

import com.example.wordcraft.DTO.Voca.*;
import com.example.wordcraft.Entity.Users;
import com.example.wordcraft.Entity.VocaWords;
import com.example.wordcraft.Entity.Vocabularies;
import com.example.wordcraft.Repository.UserRepository;
import com.example.wordcraft.Repository.VocaWordsRepository;
import com.example.wordcraft.Repository.VocabulariesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        vocabularies.setPublic(vocaCreateRequestDTO.getIsPublic());
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

    //커뮤니티 용
    public List<VocaResponseDTO> getVocaList(){
        List<Vocabularies> vocabulariesIsPublic = vocabulariesRepository.findAllByIsPublic(true);

        return vocabulariesIsPublic.stream()
                .map(vocab->{
                    int wordCount = vocaWordsRepository.countByVocabularyId(vocab.getId());
                    VocaResponseDTO vocaResponseDTO = VocaResponseDTO.from(vocab);
                    vocaResponseDTO.setWordCount(wordCount);
                    return vocaResponseDTO;
                    }
                )
                .collect(Collectors.toList());
    }

    //개인 단어장
    public List<VocaResponseDTO> getVocaListByUserId(String email){
        Users user = userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("not found user"));

        List<Vocabularies> myVocabularies = vocabulariesRepository.findAllByUser(user);
        return myVocabularies.stream()
                .map(vocab->{
                            int wordCount = vocaWordsRepository.countByVocabularyId(vocab.getId());
                            VocaResponseDTO vocaResponseDTO = VocaResponseDTO.from(vocab);
                            vocaResponseDTO.setWordCount(wordCount);
                            return vocaResponseDTO;
                        }
                )
                .collect(Collectors.toList());
    }

    //단어장 세부 조회
    @Transactional
    public VocaDetailResponseDTO getVocaDetail(Long id){
        Vocabularies vocabularies = vocabulariesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("vocabularies not found"));
        List<VocaWords> vocaWords = vocaWordsRepository.findByVocabularyId(vocabularies.getId());

        List<VocaWordRequestDTO> vocaWordRequestDTOS = vocaWords.stream()
                .map(w->{
                    VocaWordRequestDTO dto = new VocaWordRequestDTO();
                    dto.setWord(w.getWord());
                    dto.setMeaning(w.getMeanings());
                    dto.setPos(w.getPos());
                    dto.setIpa(w.getIpa());
                    dto.setExamples(w.getExamples());
                    dto.setMemoryTip(w.getMemoryTip());
                    return dto;
                })
                .toList();

        return VocaDetailResponseDTO.builder()
                .id(vocabularies.getId())
                .title(vocabularies.getTitle())
                .isPublic(vocabularies.isPublic())
                .wordCount(vocaWords.size())
                .updatedAt(vocabularies.getCreatedAt().toString().substring(0, 10))
                .author(vocabularies.getUser().getNickname())
                .words(vocaWordRequestDTOS)
                .build();
    }

    //단어장 수정
    //전부 삭제하고 다시 만드는 방식. 추후 처리시간 확인 후 변경 여부 결정.
    @Transactional
    public void updateVoca(Long id, VocabUpdateDTO vocabUpdateDTO){
        Vocabularies updateVocab = vocabulariesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("vocabularies not found"));

        updateVocab.setTitle(vocabUpdateDTO.getTitle());
        updateVocab.setCreatedAt(vocabUpdateDTO.getUpdateAt());
        updateVocab.setTag(vocabUpdateDTO.getTag());
        updateVocab.setPublic(vocabUpdateDTO.getIsPublic());

        vocaWordsRepository.deleteByVocabularyId(id);

        vocabUpdateDTO.getWords().forEach(wordDTO->{
            VocaWords vocaWords = VocaWords.builder()
                    .vocabulary(updateVocab)
                    .word(wordDTO.getWord())
                    .ipa(wordDTO.getIpa())
                    .pos(wordDTO.getPos())
                    .meanings(wordDTO.getMeanings())
                    .examples(wordDTO.getExamples())
                    .memoryTip(wordDTO.getMemoryTip())
                    .build();
            vocaWordsRepository.save(vocaWords);
        });


    }

    //단어장 삭제
    @Transactional
    public void deleteVoca(Long id){
        Vocabularies vocabularies = vocabulariesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("vocabularies not found"));
        vocaWordsRepository.deleteByVocabularyId(vocabularies.getId());
        vocabulariesRepository.delete(vocabularies);
    }

}