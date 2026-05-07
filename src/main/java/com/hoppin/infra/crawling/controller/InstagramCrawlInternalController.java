package com.hoppin.infra.crawling.controller;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/instagram-crawl")
public class InstagramCrawlInternalController {

    @PostMapping
    public ResponseEntity<?> crawl(@RequestBody CrawlRequest request) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                "node",
                "crawl-instagram.js",
                "--username", request.instagramUsername(),
                "--since", request.sinceDate(),
                "--maxPosts", "100",
                "--headless", "true"
        );

        pb.directory(new File("/Users/jinsung/Java/Spring/hoppin/scripts/instagram-crawler"));
        pb.redirectErrorStream(true);

        Process process = pb.start();

        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "인스타 크롤링 실패",
                    "output", output
            ));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "인스타 크롤링 완료",
                "output", output
        ));
    }

    public record CrawlRequest(
            String instagramUsername,
            String sinceDate
    ) {}
}