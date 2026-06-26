package com.example.wordcraft.Repository;

import com.example.wordcraft.Entity.CommunityLike;
import com.example.wordcraft.Entity.Users;
import com.example.wordcraft.Entity.Vocabularies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityLikeRepository extends JpaRepository<CommunityLike,Long> {
    Optional<CommunityLike> findByUserAndVocabulary(Users users, Vocabularies vocabulary);
    int countByVocabularyId(Long vocabularyId);
}
