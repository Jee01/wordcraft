package com.example.wordcraft.Helper;

import com.example.wordcraft.DTO.Voca.VocaDetailResponseDTO;
import com.example.wordcraft.DTO.Voca.VocaWordDetailDTO;
import com.example.wordcraft.DTO.Voca.VocaWordRequestDTO;
import com.example.wordcraft.Entity.Voca.VocaWordDetail;
import com.example.wordcraft.Entity.Voca.VocaWords;
import com.example.wordcraft.Entity.Voca.Vocabularies;
import com.example.wordcraft.Repository.VocaWordDetailRepository;
import com.example.wordcraft.Repository.VocaWordsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VocaDetailHelper {

    private final VocaWordsRepository vocaWordsRepository;
    private final VocaWordDetailRepository vocaWordDetailRepository;

    public VocaDetailResponseDTO buildDetail(Vocabularies vocabularies) {
        List<VocaWords> vocaWords = vocaWordsRepository.findByVocabularyId(vocabularies.getId());

        List<VocaWordDetail> allDetails = vocaWordDetailRepository.findByVocaWords(vocaWords);

        Map<Long, List<VocaWordDetail>> detailMap = allDetails.stream()
                .collect(Collectors.groupingBy(d -> d.getVocaWords().getId()));

        List<VocaWordRequestDTO> wordDTOs = vocaWords.stream()
                .map(w -> {
                    List<VocaWordDetailDTO> details = detailMap.getOrDefault(w.getId(), List.of())
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

        List<String> tags = (vocabularies.getTag() != null && !vocabularies.getTag().isBlank())
                ? List.of(vocabularies.getTag().split(","))
                : List.of();

        return VocaDetailResponseDTO.builder()
                .id(vocabularies.getId())
                .title(vocabularies.getTitle())
                .tags(tags)
                .isPublic(vocabularies.getIsPublic())
                .wordCount(vocaWords.size())
                .updatedAt(vocabularies.getCreatedAt().toString().substring(0, 10))
                .author(vocabularies.getUser().getNickname())
                .words(wordDTOs)
                .build();
    }
}
