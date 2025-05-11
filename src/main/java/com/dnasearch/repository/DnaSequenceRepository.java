package com.dnasearch.repository;

import com.dnasearch.model.DnaSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DnaSequenceRepository extends JpaRepository<DnaSequence, Long> {
    
    @Query("SELECT d FROM DnaSequence d WHERE d.sequence LIKE %:pattern%")
    List<DnaSequence> findByPattern(@Param("pattern") String pattern);
    
    @Query(value = "SELECT * FROM dna_sequences WHERE similarity(sequence, :sequence) > :threshold", nativeQuery = true)
    List<DnaSequence> findSimilarSequences(@Param("sequence") String sequence, @Param("threshold") double threshold);
} 