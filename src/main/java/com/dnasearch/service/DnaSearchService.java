package com.dnasearch.service;

import com.dnasearch.model.DnaSequence;
import com.dnasearch.repository.DnaSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class DnaSearchService {
    private final DnaSequenceRepository dnaSequenceRepository;
    private final ExecutorService executorService = Executors.newFixedThreadPool(8);

    public DnaSequence saveSequence(DnaSequence sequence) {
        return dnaSequenceRepository.save(sequence);
    }

    public List<DnaSequence> findByPattern(String pattern) {
        return dnaSequenceRepository.findByPattern(pattern);
    }

    @Async
    public CompletableFuture<List<DnaSequence>> findSimilarSequencesAsync(String sequence, double threshold) {
        return CompletableFuture.supplyAsync(() -> 
            dnaSequenceRepository.findSimilarSequences(sequence, threshold),
            executorService
        );
    }

    public List<DnaSequence> findSimilarSequences(String sequence, double threshold) {
        return dnaSequenceRepository.findSimilarSequences(sequence, threshold);
    }

    public void shutdown() {
        executorService.shutdown();
    }
} 