package com.example.wordcraft.Service.VocaService;

import com.example.wordcraft.DTO.Voca.*;
import com.example.wordcraft.Entity.*;
import com.example.wordcraft.Entity.Voca.VocaWordDetail;
import com.example.wordcraft.Entity.Voca.VocaWords;
import com.example.wordcraft.Entity.Voca.Vocabularies;
import com.example.wordcraft.Exception.ResourceNotFoundException;
import com.example.wordcraft.Exception.UnauthorizedException;
import com.example.wordcraft.Helper.VocaDetailHelper;
import com.example.wordcraft.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {
    private final VocabulariesRepository vocabulariesRepository;
    private final VocaWordsRepository vocaWordsRepository;
    private final VocaWordDetailRepository vocaWordDetailRepository;
    private final UserRepository userRepository;
    private final CommunityLikeRepository communityLikeRepository;
    private final VocaDetailHelper vocaDetailHelper;

    @Transactional
    public void copyVocabularies(Long id, String email) {
        Users requestUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("not found user"));

        Vocabularies originVocabularies = vocabulariesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("not found vocabularies"));

        Vocabularies copyVocabularies = Vocabularies.builder()
                .originId(originVocabularies.getId())
                .title(originVocabularies.getTitle())
                .tag(originVocabularies.getTag())
                .isPublic(originVocabularies.getIsPublic())
                .user(requestUser)
                .build();

        Vocabularies saved = vocabulariesRepository.save(copyVocabularies);

        List<VocaWords> originVocaWords = vocaWordsRepository.findByVocabularyId(originVocabularies.getId());
        List<VocaWordDetail> allDetails = vocaWordDetailRepository.findByVocaWords(originVocaWords);
        Map<Long, List<VocaWordDetail>> detailMap = allDetails.stream()
                .collect(Collectors.groupingBy(d -> d.getVocaWords().getId()));

        List<VocaWordDetail> detailList = new ArrayList<>();

        for (VocaWords originWord : originVocaWords) {
            VocaWords savedWord = vocaWordsRepository.save(VocaWords.builder()
                    .vocabulary(saved)
                    .word(originWord.getWord())
                    .ipa(originWord.getIpa())
                    .memoryTip(originWord.getMemoryTip())
                    .learned(false)
                    .build());

            for (VocaWordDetail originDetail : detailMap.getOrDefault(originWord.getId(), List.of())) {
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

        List<Long> vocabularyId = vocabulariesIsPublic.stream()
                .map(Vocabularies::getId)
                .collect(Collectors.toList());

        Map<Long, Integer> wordCountMap = vocaWordsRepository.countByVocabularyId(vocabularyId)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row ->((Long) row[1]).intValue()
                ));
        Map<Long, Integer> likeCountMap = communityLikeRepository.countByVocabularyId(vocabularyId)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((Long) row[1]).intValue()
                ));
        return vocabulariesIsPublic.stream()
                .map(vocab->{
                    VocaResponseDTO dto = VocaResponseDTO.from(vocab);
                    dto.setWordCount(wordCountMap.getOrDefault(vocab.getId(), 0));
                    dto.setLikeCount(likeCountMap.getOrDefault(vocab.getId(), 0));
                    return dto;
                }).collect(Collectors.toList());
    }

    @Transactional
    public VocaDetailResponseDTO getVocaDetail(Long id){
        Vocabularies vocabularies = getVocabularies(id);
        if(Objects.equals(vocabularies.getIsPublic(),false)){
            throw new UnauthorizedException("private vocabularies");
        }
        return vocaDetailHelper.buildDetail(vocabularies);
    }

    @Transactional
    public Boolean likeVoca(Long id, String email){
        Vocabularies vocabularies = getVocabularies(id);
        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("not found user"));

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
                .orElseThrow(() -> new ResourceNotFoundException("not found vocabularies"));
    }
}
