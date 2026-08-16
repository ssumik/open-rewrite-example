package com.example.demo.service;

import com.example.demo.config.properties.ImageApiProperties;
import com.example.demo.model.AvatarRequest;
import com.example.demo.model.AvatarShape;
import com.example.demo.model.SvgImage;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AvatarService {
    private final ImageApiProperties properties;
    private final SvgSanitizer svgSanitizer;

    public AvatarService(ImageApiProperties properties, SvgSanitizer svgSanitizer) {
        this.properties = properties;
        this.svgSanitizer = svgSanitizer;
    }

    public SvgImage generate(AvatarRequest request) {
        int size = bounded(Objects.requireNonNullElse(request.size(), properties.defaultAvatarSize()));
        String initials = initials(request.name());
        String background = Objects.requireNonNullElseGet(request.background(), () -> paletteColor(request.name()));
        String color = Objects.requireNonNullElse(request.color(), "#ffffff");
        int fontSize = Math.max(18, Math.round(size * 0.42f));
        String mask = request.shape() == AvatarShape.CIRCLE
                ? "<circle cx=\"50%\" cy=\"50%\" r=\"50%\" fill=\"" + background + "\"/>"
                : "<rect width=\"100%\" height=\"100%\" rx=\"" + Math.round(size * 0.14f) + "\" fill=\"" + background + "\"/>";

        String svg = """
                <svg xmlns="http://www.w3.org/2000/svg" width="%d" height="%d" viewBox="0 0 %d %d" role="img" aria-label="%s avatar">
                  %s
                  <text x="50%%" y="52%%" dominant-baseline="middle" text-anchor="middle" fill="%s" font-family="Inter, Arial, sans-serif" font-size="%d" font-weight="700">%s</text>
                </svg>
                """.formatted(
                size,
                size,
                size,
                size,
                svgSanitizer.escapeAttribute(request.name()),
                mask,
                color,
                fontSize,
                svgSanitizer.escapeText(initials)
        );

        return new SvgImage(svg.strip(), size, size);
    }

    private int bounded(int size) {
        return Math.min(properties.maxSize(), Math.max(properties.minSize(), size));
    }

    private String paletteColor(String seed) {
        int index = Math.floorMod(seed.toLowerCase(Locale.ROOT).hashCode(), properties.palette().size());
        return properties.palette().get(index);
    }

    private static String initials(String name) {
        String cleaned = name.strip();
        if (cleaned.isEmpty()) {
            return "?";
        }

        String initials = Arrays.stream(cleaned.split("\\s+"))
                .filter(part -> !part.isBlank())
                .limit(2)
                .map(part -> part.substring(0, part.offsetByCodePoints(0, 1)))
                .collect(Collectors.joining())
                .toUpperCase(Locale.ROOT);

        return initials.isBlank() ? "?" : initials;
    }
}
