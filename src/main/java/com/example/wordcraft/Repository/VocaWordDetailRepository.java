package com.example.wordcraft.Repository;

import com.example.wordcraft.Entity.Voca.VocaWordDetail;
import com.example.wordcraft.Entity.Voca.VocaWords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocaWordDetailRepository extends JpaRepository<VocaWordDetail, Long> {
    @Query("SELECT d FROM VocaWordDetail d WHERE d.vocaWords IN :vocaWords")
    List<VocaWordDetail> findByVocaWords(@Param("vocaWords") List<VocaWords> vocaWords);
}
