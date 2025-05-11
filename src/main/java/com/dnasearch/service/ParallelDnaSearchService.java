package com.dnasearch.service;

import com.dnasearch.model.DnaSequence;
import com.dnasearch.repository.DnaSequenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParallelDnaSearchService {
    private final DnaSequenceRepository dnaSequenceRepository;
    private final ConcurrentHashMap<String, AtomicInteger> searchMetrics = new ConcurrentHashMap<>();

    @Async("dnaSearchExecutor")
    public CompletableFuture<List<DnaSequence>> parallelSearch(String pattern, int chunkSize) {
        log.info("Starting parallel search for pattern: {}", pattern);
        searchMetrics.computeIfAbsent(pattern, k -> new AtomicInteger(0)).incrementAndGet();

        return CompletableFuture.supplyAsync(() -> {
            List<DnaSequence> allSequences = dnaSequenceRepository.findAll();
            List<CompletableFuture<List<DnaSequence>>> futures = new ArrayList<>();

            // Split the work into chunks
            for (int i = 0; i < allSequences.size(); i += chunkSize) {
                int end = Math.min(i + chunkSize, allSequences.size());
                List<DnaSequence> chunk = allSequences.subList(i, end);
                
                CompletableFuture<List<DnaSequence>> future = CompletableFuture.supplyAsync(() ->
                    chunk.stream()
                        .filter(sequence -> sequence.getSequence().contains(pattern))
                        .collect(Collectors.toList())
                );
                futures.add(future);
            }

            // Combine all results
            return futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        });
    }

    @Async("sequenceAnalysisExecutor")
    public CompletableFuture<List<DnaSequence>> findSimilarSequencesParallel(
            String sequence, 
            double threshold,
            int maxThreads) {
        
        log.info("Starting parallel similarity search with threshold: {}", threshold);
        
        return CompletableFuture.supplyAsync(() -> {
            List<DnaSequence> allSequences = dnaSequenceRepository.findAll();
            int chunkSize = Math.max(1, allSequences.size() / maxThreads);
            
            List<CompletableFuture<List<DnaSequence>>> futures = new ArrayList<>();
            
            for (int i = 0; i < allSequences.size(); i += chunkSize) {
                int end = Math.min(i + chunkSize, allSequences.size());
                List<DnaSequence> chunk = allSequences.subList(i, end);
                
                CompletableFuture<List<DnaSequence>> future = CompletableFuture.supplyAsync(() ->
                    chunk.stream()
                        .filter(seq -> calculateSimilarity(seq.getSequence(), sequence) > threshold)
                        .collect(Collectors.toList())
                );
                futures.add(future);
            }
            
            return futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        });
    }

    private double calculateSimilarity(String seq1, String seq2) {
        // Implement Levenshtein distance or other similarity metric
        int maxLength = Math.max(seq1.length(), seq2.length());
        if (maxLength == 0) return 1.0;
        
        int distance = levenshteinDistance(seq1, seq2);
        return 1.0 - ((double) distance / maxLength);
    }

    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], 
                                  Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }
        return dp[s1.length()][s2.length()];
    }

    public ConcurrentHashMap<String, AtomicInteger> getSearchMetrics() {
        return searchMetrics;
    }
} 