package com.example.wordcraft.Repository;

import com.example.wordcraft.Entity.VocaWordDetail;
import com.example.wordcraft.Entity.VocaWords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocaWordDetailRepository extends JpaRepository<VocaWordDetail, Long> {
    List<VocaWordDetail> findByVocaWords(VocaWords vocaWords);
}
