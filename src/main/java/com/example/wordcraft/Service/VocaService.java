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
import java.util.Objects;
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

        Vocabularies vocabularies = Vocabularies.builder()
                .originId(null)
                .title(vocaCreateRequestDTO.getTitle())
                .tag(vocaCreateRequestDTO.getTag())
                .isPublic(vocaCreateRequestDTO.getIsPublic())
                .user(user)
                .build();

        Vocabularies saved = vocabulariesRepository.save(vocabularies);

        List<VocaWords> vocaWordsList = vocaCreateRequestDTO.getWords().stream()
                .map(wordDTO -> VocaWords.builder()
                        .vocabulary(saved)
                        .word(wordDTO.getWord())
                        .meanings(wordDTO.getMeaning())
                        .pos(wordDTO.getPos())
                        .ipa(wordDTO.getIpa())
                        .examples(wordDTO.getExamples())
                        .memoryTip(wordDTO.getMemoryTip())
                        .learned(false)
                        .build())
                .toList();

        vocaWordsRepository.saveAll(vocaWordsList);
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
    public VocaDetailResponseDTO getVocaDetail(String email, Long id){
        Vocabularies vocabularies = getVocabularies(id);

        userValid(email, vocabularies);//이후 예외 처리 대시보드로 보내도록 변경

        List<VocaWords> vocaWords = vocaWordsRepository.findByVocabularyId(vocabularies.getId());

        List<VocaWordRequestDTO> vocaWordRequestDTOS = vocaWords.stream()
                .map(w->{
                    VocaWordRequestDTO dto = new VocaWordRequestDTO();
                    dto.setId(w.getId());
                    dto.setWord(w.getWord());
                    dto.setMeaning(w.getMeanings());
                    dto.setPos(w.getPos());
                    dto.setIpa(w.getIpa());
                    dto.setExamples(w.getExamples());
                    dto.setMemoryTip(w.getMemoryTip());
                    dto.setLearned(w.getLearned());
                    return dto;
                })
                .toList();

        return VocaDetailResponseDTO.builder()
                .id(vocabularies.getId())
                .title(vocabularies.getTitle())
                .isPublic(vocabularies.getIsPublic())
                .wordCount(vocaWords.size())
                .updatedAt(vocabularies.getCreatedAt().toString().substring(0, 10))
                .author(vocabularies.getUser().getNickname())
                .words(vocaWordRequestDTOS)
                .build();
    }

    //단어장 수정
    //전부 삭제하고 다시 만드는 방식. 추후 처리시간 확인 후 변경 여부 결정.
    @Transactional
    public void updateVoca(String email, Long id, VocabUpdateDTO vocabUpdateDTO){
        Vocabularies updateVocab = getVocabularies(id);

        userValid(email, updateVocab);

        updateVocab.setTitle(vocabUpdateDTO.getTitle());
        updateVocab.setCreatedAt(vocabUpdateDTO.getUpdateAt());
        updateVocab.setTag(vocabUpdateDTO.getTag());
        updateVocab.setIsPublic(vocabUpdateDTO.getIsPublic());

        vocaWordsRepository.deleteByVocabularyId(id);

        vocabUpdateDTO.getWords().forEach(wordDTO->{
            VocaWords vocaWords = VocaWords.builder()
                    .vocabulary(updateVocab)
                    .id(wordDTO.getId())
                    .word(wordDTO.getWord())
                    .ipa(wordDTO.getIpa())
                    .pos(wordDTO.getPos())
                    .meanings(wordDTO.getMeanings())
                    .examples(wordDTO.getExamples())
                    .memoryTip(wordDTO.getMemoryTip())
                    .learned(wordDTO.getLearned())
                    .build();
            vocaWordsRepository.save(vocaWords);
        });
    }

    @Transactional
    public void updateVocaWordLearn(Long id, VocaWordLearnDTO vocabLearnDTO, String email){
        Vocabularies updateVocaL = getVocabularies(id);

        userValid(email, updateVocaL);

        VocaWords updateWordL = vocaWordsRepository.findVocaWordsById(updateVocaL, vocabLearnDTO.getId())
                .orElseThrow(() -> new RuntimeException("word not found"));

        Boolean status = vocabLearnDTO.getLearned();

        updateWordL.setLearned(status);
        vocaWordsRepository.save(updateWordL);
    }

    //단어장 삭제
    @Transactional
    public void deleteVoca(Long id, String email){
        Vocabularies vocabularies = getVocabularies(id);

        userValid(email, vocabularies);

        vocaWordsRepository.deleteByVocabularyId(vocabularies.getId());
        vocabulariesRepository.delete(vocabularies);
    }

    private Vocabularies getVocabularies(Long id){
        return vocabulariesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("vocabularies not found"));
    }
    private void userValid(String email, Vocabularies vocabularies){
        if (!Objects.equals(vocabularies.getUser().getEmail(), email)){
            throw new RuntimeException("user's match error");
        }
    }

}