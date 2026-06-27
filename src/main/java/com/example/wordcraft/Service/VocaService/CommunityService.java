package com.example.wordcraft.Service.VocaService;

import com.example.wordcraft.DTO.Voca.VocaDetailResponseDTO;
import com.example.wordcraft.DTO.Voca.VocaResponseDTO;
import com.example.wordcraft.DTO.Voca.VocaWordRequestDTO;
import com.example.wordcraft.Entity.CommunityLike;
import com.example.wordcraft.Entity.Users;
import com.example.wordcraft.Entity.VocaWords;
import com.example.wordcraft.Entity.Vocabularies;
import com.example.wordcraft.Repository.CommunityLikeRepository;
import com.example.wordcraft.Repository.UserRepository;
import com.example.wordcraft.Repository.VocaWordsRepository;
import com.example.wordcraft.Repository.VocabulariesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {
    private final VocabulariesRepository vocabulariesRepository;
    private final VocaWordsRepository vocaWordsRepository;
    private final UserRepository userRepository;
    private final CommunityLikeRepository communityLikeRepository;

    @Transactional
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
                        .learned(false)
                        .build())
                .toList();

        vocaWordsRepository.saveAll(copyWordsList);
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
