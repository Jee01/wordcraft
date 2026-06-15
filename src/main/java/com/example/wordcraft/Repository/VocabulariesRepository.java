package com.example.wordcraft.Repository;

import com.example.wordcraft.Entity.Vocabularies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabulariesRepository extends JpaRepository<Vocabularies, Long> {
    @Query("SELECT v FROM Vocabularies v WHERE v.isPublic = :isPublic")
    List<Vocabularies> findAllByIsPublic(@Param("isPublic") boolean isPublic);
}
