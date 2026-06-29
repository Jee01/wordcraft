package com.example.wordcraft.Service.VocaService;

import com.example.wordcraft.DTO.Voca.VocaDetailResponseDTO;
import com.example.wordcraft.DTO.Voca.VocaResponseDTO;
import com.example.wordcraft.DTO.Voca.VocaWordDetailDTO;
import com.example.wordcraft.DTO.Voca.VocaWordRequestDTO;
import com.example.wordcraft.Entity.*;
import com.example.wordcraft.Entity.Voca.VocaWordDetail;
import com.example.wordcraft.Entity.Voca.VocaWords;
import com.example.wordcraft.Entity.Voca.Vocabularies;
import com.example.wordcraft.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {
    private final VocabulariesRepository vocabulariesRepository;
    private final VocaWordsRepository vocaWordsRepository;
    private final VocaWordDetailRepository vocaWordDetailRepository;
    private final UserRepository userRepository;
    private final CommunityLikeRepository communityLikeRepository;

    @Transactional
    public void copyVocabularies(Long id, String email) {
        Users requestUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("not found user"));

        Vocabularies originVocabularies = vocabulariesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("vocabularies not found"));

        Vocabularies copyVocabularies = Vocabularies.builder()
                .originId(originVocabularies.getId())
                .title(originVocabularies.getTitle())
                .tag(originVocabularies.getTag())
                .isPublic(originVocabularies.getIsPublic())
                .user(requestUser)
                .build();

        Vocabularies saved = vocabulariesRepository.save(copyVocabularies);

        List<VocaWords> originVocaWords = vocaWordsRepository.findByVocabularyId(originVocabularies.getId());
        List<VocaWordDetail> detailList = new ArrayList<>();

        for (VocaWords originWord : originVocaWords) {
            VocaWords savedWord = vocaWordsRepository.save(VocaWords.builder()
                    .vocabulary(saved)
                    .word(originWord.getWord())
                    .ipa(originWord.getIpa())
                    .memoryTip(originWord.getMemoryTip())
                    .learned(false)
                    .build());

            for (VocaWordDetail originDetail : vocaWordDetailRepository.findByVocaWords(originWord)) {
                detailList.add(VocaWordDetail.builder()
                        .vocaWords(savedWord)
                        .pos(originDetail.getPos())
                        .meanings(originDetail.getMeanings())
                        .examples(originDetail.getExamples())
                        .build());
            }
        }

        vocaWordDetailRepository.saveAll(detailList);
    }

    public List<VocaResponseDTO> getVocaList(){
        List<Vocabularies> vocabulariesIsPublic = vocabulariesRepository.findAllByIsPublic(true);

        return vocabulariesIsPublic.stream()
                .map(vocab->{
                            int wordCount = vocaWordsRepository.countByVocabularyId(vocab.getId());
                            int likeCount = communityLikeRepository.countByVocabularyId(vocab.getId());
                            VocaResponseDTO vocaResponseDTO = VocaResponseDTO.from(vocab);
                            vocaResponseDTO.setWordCount(wordCount);
                            vocaResponseDTO.setLikeCount(likeCount);
                            return vocaResponseDTO;
                        }
                )
                .collect(Collectors.toList());
    }

    @Transactional
    public VocaDetailResponseDTO getVocaDetail(Long id){
        Vocabularies vocabularies = getVocabularies(id);
        if(Objects.equals(vocabularies.getIsPublic(),false)){
            throw new RuntimeException("private vocab"); //이후 예외 처리 대시보드로 보내도록 변경
        }
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

    @Transactional
    public Boolean likeVoca(Long id, String email){
        Vocabularies vocabularies = getVocabularies(id);
        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("email not found"));

        Optional<CommunityLike> communityLike = communityLikeRepository.findByUserAndVocabulary(users, vocabularies);

        if (communityLike.isPresent()) {
            communityLikeRepository.delete(communityLike.get());
            return false;
        } else {
            CommunityLike newLike = CommunityLike.builder()
                    .vocabulary(vocabularies)
                    .user(users)
                    .build();
            communityLikeRepository.save(newLike);
            return true;
        }
    }

    protected Vocabularies getVocabularies(Long id){
        return vocabulariesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("vocabularies not found"));
    }
}
