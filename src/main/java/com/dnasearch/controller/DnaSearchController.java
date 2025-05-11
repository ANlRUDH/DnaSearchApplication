package com.dnasearch.controller;

import com.dnasearch.model.DnaSequence;
import com.dnasearch.service.ParallelDnaSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/api/dna")
@RequiredArgsConstructor
public class DnaSearchController {
    private final ParallelDnaSearchService parallelDnaSearchService;

    @PostMapping
    public ResponseEntity<DnaSequence> saveSequence(@RequestBody DnaSequence sequence) {
        return ResponseEntity.ok(dnaSearchService.saveSequence(sequence));
    }

    @GetMapping("/search")
    public ResponseEntity<List<DnaSequence>> searchByPattern(@RequestParam String pattern) {
        return ResponseEntity.ok(dnaSearchService.findByPattern(pattern));
    }

    @GetMapping("/similar")
    public ResponseEntity<List<DnaSequence>> findSimilarSequences(
            @RequestParam String sequence,
            @RequestParam(defaultValue = "0.8") double threshold) {
        return ResponseEntity.ok(dnaSearchService.findSimilarSequences(sequence, threshold));
    }

    @GetMapping("/similar/async")
    public CompletableFuture<ResponseEntity<List<DnaSequence>>> findSimilarSequencesAsync(
            @RequestParam String sequence,
            @RequestParam(defaultValue = "0.8") double threshold) {
        return dnaSearchService.findSimilarSequencesAsync(sequence, threshold)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/search/parallel")
    public CompletableFuture<ResponseEntity<List<DnaSequence>>> parallelSearch(
            @RequestParam String pattern,
            @RequestParam(defaultValue = "100") int chunkSize) {
        log.info("Received parallel search request for pattern: {}", pattern);
        return parallelDnaSearchService.parallelSearch(pattern, chunkSize)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/similar/parallel")
    public CompletableFuture<ResponseEntity<List<DnaSequence>>> findSimilarSequencesParallel(
            @RequestParam String sequence,
            @RequestParam(defaultValue = "0.8") double threshold,
            @RequestParam(defaultValue = "4") int maxThreads) {
        log.info("Received parallel similarity search request with threshold: {}", threshold);
        return parallelDnaSearchService.findSimilarSequencesParallel(sequence, threshold, maxThreads)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Integer>> getSearchMetrics() {
        Map<String, Integer> metrics = parallelDnaSearchService.getSearchMetrics()
                .entrySet()
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                    Map.Entry::getKey,
                    e -> e.getValue().get()
                ));
        return ResponseEntity.ok(metrics);
    }
} 