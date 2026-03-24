package com.url_shorter.urlshorter.model.response;

import java.time.LocalDateTime;

public record UrlResponse(

        String shortCode,
        String originalUrl,
        String shortUrl,
        Long clickCount,
        LocalDateTime createAt
) {}
