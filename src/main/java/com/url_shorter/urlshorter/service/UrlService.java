package com.url_shorter.urlshorter.service;

import com.url_shorter.urlshorter.entity.UrlEntity;
import com.url_shorter.urlshorter.model.request.UrlRequest;
import com.url_shorter.urlshorter.model.response.ApiResponse;
import com.url_shorter.urlshorter.model.response.UrlResponse;
import com.url_shorter.urlshorter.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.base-url}")
    private String baseUrl;

    public UrlResponse shorterUrl(UrlRequest request){
        log.info("Action.shorterUrl.start for original url {}", request.originalUrl());
        String originalUrl = request.originalUrl();

        log.info("Action.shorterUrl.end for original url {}", request.originalUrl());
        return urlRepository.findByOriginalUrl(originalUrl)
                        .map(this::toResponse)
                                .orElseGet(() -> {
                                    String shortCode = generateUniqueCode();

                                    UrlEntity url = new UrlEntity();
                                    url.setShortCode(shortCode);
                                    url.setOriginalUrl(originalUrl);

                                    redisTemplate.opsForValue()
                                            .set(shortCode, originalUrl, 7, TimeUnit.DAYS);

                                    log.info("Action.shorterUrl.end for original url {}", request.originalUrl());
                                    return toResponse(urlRepository.save(url));
                                });
    }

    // Redirect
    public String getOriginalUrl(String shortCode) {
        String cached = redisTemplate.opsForValue().get(shortCode);

        if (cached != null) {
            incrementClick(shortCode);
            return cached;
        }

        UrlEntity url = findByCodeOrThrow(shortCode);
        redisTemplate.opsForValue()
                .set(shortCode, url.getOriginalUrl(), 7, TimeUnit.DAYS);
        incrementClick(shortCode);
        return url.getOriginalUrl();
    }

    // Statistika
    public UrlResponse getStats(String shortCode) {
        return toResponse(findByCodeOrThrow(shortCode));
    }

    // Bütün URL-lər
    public List<UrlResponse> getAllUrls() {
        return urlRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // --- Köməkçi metodlar ---

    private void incrementClick(String shortCode) {
        urlRepository.findByShortCode(shortCode).ifPresent(url -> {
            url.setClickCount(url.getClickCount() + 1);
            urlRepository.save(url);
        });
    }

    private UrlEntity findByCodeOrThrow(String shortCode) {
        return urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("URL tapılmadı: " + shortCode));
    }

    private UrlResponse toResponse(UrlEntity url) {
        return new UrlResponse(
                url.getShortCode(),
                url.getOriginalUrl(),
                baseUrl + "/" + url.getShortCode(),
                url.getClickCount(),
                url.getCreateAt()
        );
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().substring(0, 7);
        } while (urlRepository.existsByShortCode(code));
        return code;
    }
}
