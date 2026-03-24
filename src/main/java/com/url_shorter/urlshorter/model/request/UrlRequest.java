package com.url_shorter.urlshorter.model.request;

import jakarta.validation.constraints.NotBlank;

public record UrlRequest(

        @NotBlank
        String originalUrl

) {}