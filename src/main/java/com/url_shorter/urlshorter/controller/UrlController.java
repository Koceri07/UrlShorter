package com.url_shorter.urlshorter.controller;

import com.url_shorter.urlshorter.model.request.UrlRequest;
import com.url_shorter.urlshorter.model.response.UrlResponse;
import com.url_shorter.urlshorter.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    // URL qısalt
    @PostMapping("/shorten")
    public ResponseEntity<UrlResponse> shorten(@RequestBody @Valid UrlRequest request) {
        return ResponseEntity.ok(urlService.shorterUrl(request));
    }

    // Redirect
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String originalUrl = urlService.getOriginalUrl(shortCode);
        return ResponseEntity
                .status(HttpStatus.FOUND)           // 302 redirect
                .location(URI.create(originalUrl))
                .build();
    }

    // Statistika
    @GetMapping("/{shortCode}/stats")
    public ResponseEntity<UrlResponse> getStats(@PathVariable String shortCode) {
        return ResponseEntity.ok(urlService.getStats(shortCode));
    }

    // Bütün URL-lər
    @GetMapping("/urls")
    public ResponseEntity<List<UrlResponse>> getAllUrls() {
        return ResponseEntity.ok(urlService.getAllUrls());
    }
}
