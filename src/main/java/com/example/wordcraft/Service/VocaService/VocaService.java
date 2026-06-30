package com.example.wordcraft.Service.VocaService;

import com.example.wordcraft.DTO.Voca.*;
import com.example.wordcraft.Entity.Users;
import com.example.wordcraft.Entity.Voca.VocaWordDetail;
import com.example.wordcraft.Entity.Voca.VocaWords;
import com.example.wordcraft.Entity.Voca.Vocabularies;
import com.example.wordcraft.Exception.ResourceNotFoundException;
import com.example.wordcraft.Exception.UnauthorizedException;
import com.example.wordcraft.Repository.UserRepository;
import com.example.wordcraft.Repository.VocaWordDetailRepository;
import com.example.wordcraft.Repository.VocaWordsRepository;
import com.example.wordcraft.Repository.VocabulariesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VocaService {
    private final VocabulariesRepository vocabulariesRepository;
    private final VocaWordsRepository vocaWordsRepository;
    private final VocaWordDetailRepository vocaWordDetailRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createVocabularies(VocaRequestDTO vocaRequestDTO, String email)
    {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("존재하지 않는 사용자입니다."));

        Vocabularies vocabularies = Vocabularies.builder()
                .originId(null)
                .title(vocaRequestDTO.getTitle())
                .tag(vocaRequestDTO.getTag())
                .isPublic(vocaRequestDTO.getIsPublic())
                .user(user)
                .build();

        Vocabularies saved = vocabulariesRepository.save(vocabularies);

        List<VocaWordDetail> detailList = new ArrayList<>();

        for (VocaWordRequestDTO wordDTO : vocaRequestDTO.getWords()) {
            VocaWords savedWord = vocaWordsRepository.save(VocaWords.builder()
                    .vocabulary(saved)
                    .word(wordDTO.getWord())
                    .ipa(wordDTO.getIpa())
                    .memoryTip(wordDTO.getMemoryTip())
                    .learned(false)
                    .build());

            for (VocaWordDetailDTO detailDTO : wordDTO.getVocaWordDetailDTOS()) {
                detailList.add(VocaWordDetail.builder()
                        .vocaWords(savedWord)
                        .pos(detailDTO.getPos())
                        .meanings(detailDTO.getMeaning())
                        .examples(detailDTO.getExamples())
                        .build());
            }
        }

        vocaWordDetailRepository.saveAll(detailList);
    }

    //개인 단어장
    public List<VocaResponseDTO> getVocaListByUserId(String email){
        Users user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("존재하지 않는 사용자입니다."));

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
    public VocaDetailResponseDTO getVocaDetail(String email, Long id) {
        Vocabularies vocabularies = getVocabularies(id);

        userValid(email, vocabularies);

        List<VocaWords> vocaWords = vocaWordsRepository.findByVocabularyId(vocabularies.getId());

        List<VocaWordRequestDTO> vocaWordRequestDTOS = vocaWords.stream()
                .map(w -> {
                    List<VocaWordDetailDTO> details = vocaWordDetailRepository.findByVocaWords(w)
                            .stream()
                            .map(detail -> {
                                VocaWordDetailDTO dto = new VocaWordDetailDTO();
                                dto.setId(detail.getId());
                                dto.setPos(detail.getPos());
                                dto.setMeaning(detail.getMeanings());
                                dto.setExamples(detail.getExamples());
                                return dto;
                            })
                            .toList();

                    VocaWordRequestDTO dto = new VocaWordRequestDTO();
                    dto.setId(w.getId());
                    dto.setWord(w.getWord());
                    dto.setIpa(w.getIpa());
                    dto.setMemoryTip(w.getMemoryTip());
                    dto.setLearned(w.getLearned());
                    dto.setVocaWordDetailDTOS(details);
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
    public void updateVoca(String email, Long id, VocaRequestDTO vocabUpdateDTO){
        Vocabularies updateVocab = getVocabularies(id);

        userValid(email, updateVocab);

        updateVocab.setTitle(vocabUpdateDTO.getTitle());
        updateVocab.setTag(vocabUpdateDTO.getTag());
        updateVocab.setIsPublic(vocabUpdateDTO.getIsPublic());

        vocaWordsRepository.deleteByVocabularyId(id);

        List<VocaWordDetail> detailList = new ArrayList<>();

        for (VocaWordRequestDTO wordDTO : vocabUpdateDTO.getWords()) {
            VocaWords updateWord = vocaWordsRepository.save(VocaWords.builder()
                    .vocabulary(updateVocab)
                    .word(wordDTO.getWord())
                    .ipa(wordDTO.getIpa())
                    .memoryTip(wordDTO.getMemoryTip())
                    .learned(wordDTO.getLearned())
                    .build());

            for (VocaWordDetailDTO detailDTO : wordDTO.getVocaWordDetailDTOS()) {
                detailList.add(VocaWordDetail.builder()
                        .vocaWords(updateWord)
                        .pos(detailDTO.getPos())
                        .meanings(detailDTO.getMeaning())
                        .examples(detailDTO.getExamples())
                        .build());
            }
        }
        vocaWordDetailRepository.saveAll(detailList);
    }

    @Transactional
    public void updateVocaWordLearn(Long id, VocaWordLearnDTO vocabLearnDTO, String email){
        Vocabularies updateVocaL = getVocabularies(id);

        userValid(email, updateVocaL);

        VocaWords updateWordL = vocaWordsRepository.findVocaWordsById(updateVocaL, vocabLearnDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 단어입니다."));

        Boolean status = vocabLearnDTO.getLearned();

        updateWordL.setLearned(status);
        vocaWordsRepository.save(updateWordL);
    }

    //단어장 삭제
    @Transactional
    public void deleteVoca(Long id, String email){
        Vocabularies vocabularies = getVocabularies(id);

        userValid(email, vocabularies);

        vocabulariesRepository.delete(vocabularies);
    }

    private Vocabularies getVocabularies(Long id){
        return vocabulariesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("not found vocabularies"));
    }
    private void userValid(String email, Vocabularies vocabularies){
        if (!Objects.equals(vocabularies.getUser().getEmail(), email)){
            throw new UnauthorizedException("해당 단어장에 대한 권한이 없습니다.");
        }
    }

}