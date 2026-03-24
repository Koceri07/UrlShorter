package com.url_shorter.urlshorter.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Table(name = "urls")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UrlEntity {

    @GeneratedValue
    @Id
    private Long id;

    private String shortCode;

    private String originalUrl;

    private Long clickCount = 0L;

    @CreationTimestamp
    private LocalDateTime createAt;


}
