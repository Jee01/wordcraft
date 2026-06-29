package com.example.wordcraft.Repository;

import com.example.wordcraft.Entity.Voca.VocaWordDetail;
import com.example.wordcraft.Entity.Voca.VocaWords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocaWordDetailRepository extends JpaRepository<VocaWordDetail, Long> {
    List<VocaWordDetail> findByVocaWords(VocaWords vocaWords);
}
