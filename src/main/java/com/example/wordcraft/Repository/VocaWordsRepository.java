package com.example.wordcraft.Repository;

import com.example.wordcraft.Entity.VocaWords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocaWordsRepository extends JpaRepository<VocaWords,Long> {
    int countByVocabularyId(Long vocabularyId);
    List<VocaWords> findByVocabularyId(Long vocabularyId);
    void deleteByVocabularyId(Long vocabularyId);
}
