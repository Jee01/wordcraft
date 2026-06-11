package com.example.wordcraft.Repository;

import com.example.wordcraft.Entity.VocaWords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VocaWordsRepository extends JpaRepository<VocaWords,Long> {
}
