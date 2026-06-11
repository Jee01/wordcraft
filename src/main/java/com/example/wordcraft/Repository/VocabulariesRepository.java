package com.example.wordcraft.Repository;

import com.example.wordcraft.Entity.Vocabularies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VocabulariesRepository extends JpaRepository<Vocabularies, Long> {
}
