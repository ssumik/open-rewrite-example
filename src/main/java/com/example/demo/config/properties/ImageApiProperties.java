package com.example.demo.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "image-api")
public record ImageApiProperties(
        int defaultAvatarSize,
        int minSize,
        int maxSize,
        int defaultPlaceholderWidth,
        int defaultPlaceholderHeight,
        List<String> palette
) {
    public ImageApiProperties {
        if (palette == null || palette.isEmpty()) {
            palette = List.of("#2563eb", "#16a34a", "#dc2626", "#9333ea", "#ea580c", "#0891b2");
        }
    }
}
