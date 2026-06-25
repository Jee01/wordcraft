package com.example.wordcraft.Repository;

import com.example.wordcraft.Entity.VocaWords;
import com.example.wordcraft.Entity.Vocabularies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VocaWordsRepository extends JpaRepository<VocaWords,Long> {
    int countByVocabularyId(Long vocabularyId);
    List<VocaWords> findByVocabularyId(Long vocabularyId);
    void deleteByVocabularyId(Long vocabularyId);

    @Query("SELECT v FROM VocaWords v WHERE v.vocabulary = :vocabulary AND v.id = :wordId")
    Optional<VocaWords> findVocaWordsById(@Param("vocabulary")Vocabularies vocabularies,
                                          @Param("wordId") Long wordId);

}
