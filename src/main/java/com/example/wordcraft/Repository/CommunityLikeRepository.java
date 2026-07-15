package com.example.wordcraft.Repository;

import com.example.wordcraft.Entity.CommunityLike;
import com.example.wordcraft.Entity.Users;
import com.example.wordcraft.Entity.Voca.Vocabularies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityLikeRepository extends JpaRepository<CommunityLike,Long> {
    Optional<CommunityLike> findByUserAndVocabulary(Users users, Vocabularies vocabulary);
    @Query("SELECT c.vocabulary.id, COUNT(c) FROM CommunityLike c WHERE c.vocabulary.id IN :vocabularyId GROUP BY c.vocabulary.id")
    List<Object[]> countByVocabularyId(@Param("vocabularyId") List<Long> vocabularyId);
}
