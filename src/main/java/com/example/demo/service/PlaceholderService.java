package com.example.demo.service;

import com.example.demo.config.properties.ImageApiProperties;
import com.example.demo.model.PlaceholderRequest;
import com.example.demo.model.SvgImage;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PlaceholderService {
    private final ImageApiProperties properties;
    private final SvgSanitizer svgSanitizer;

    public PlaceholderService(ImageApiProperties properties, SvgSanitizer svgSanitizer) {
        this.properties = properties;
        this.svgSanitizer = svgSanitizer;
    }

    public SvgImage generate(PlaceholderRequest request) {
        int width = bounded(Objects.requireNonNullElse(request.width(), properties.defaultPlaceholderWidth()));
        int height = bounded(Objects.requireNonNullElse(request.height(), properties.defaultPlaceholderHeight()));
        String label = Objects.requireNonNullElse(request.label(), width + " x " + height);
        int fontSize = Math.max(16, Math.min(width, height) / 10);
        int strokeWidth = Math.max(2, Math.min(width, height) / 160);

        String svg = """
                <svg xmlns="http://www.w3.org/2000/svg" width="%d" height="%d" viewBox="0 0 %d %d" role="img" aria-label="%s">
                  <rect width="100%%" height="100%%" fill="%s"/>
                  <path d="M0 0 L%d %d M%d 0 L0 %d" stroke="%s" stroke-opacity="0.24" stroke-width="%d"/>
                  <rect x="%d" y="%d" width="%d" height="%d" rx="%d" fill="#ffffff" fill-opacity="0.48"/>
                  <text x="50%%" y="50%%" dominant-baseline="middle" text-anchor="middle" fill="%s" font-family="Inter, Arial, sans-serif" font-size="%d" font-weight="700">%s</text>
                </svg>
                """.formatted(
                width,
                height,
                width,
                height,
                svgSanitizer.escapeAttribute(label),
                request.background(),
                width,
                height,
                width,
                height,
                request.color(),
                strokeWidth,
                Math.round(width * 0.08f),
                Math.round(height * 0.08f),
                Math.round(width * 0.84f),
                Math.round(height * 0.84f),
                Math.max(8, Math.min(width, height) / 24),
                request.color(),
                fontSize,
                svgSanitizer.escapeText(label)
        );

        return new SvgImage(svg.strip(), width, height);
    }

    private int bounded(int value) {
        return Math.min(properties.maxSize(), Math.max(properties.minSize(), value));
    }
}
