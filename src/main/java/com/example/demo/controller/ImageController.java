package com.example.demo.controller;

import com.example.demo.model.AvatarRequest;
import com.example.demo.model.AvatarShape;
import com.example.demo.model.PlaceholderRequest;
import com.example.demo.model.SvgImage;
import com.example.demo.service.AvatarService;
import com.example.demo.service.PlaceholderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@Validated
@RestController
@RequestMapping("/api/v1")
public class ImageController {
    private static final MediaType SVG_MEDIA_TYPE = MediaType.valueOf("image/svg+xml");

    private final AvatarService avatarService;
    private final PlaceholderService placeholderService;

    public ImageController(AvatarService avatarService, PlaceholderService placeholderService) {
        this.avatarService = avatarService;
        this.placeholderService = placeholderService;
    }

    @Operation(summary = "Generate an SVG initials avatar")
    @GetMapping(value = "/avatar", produces = "image/svg+xml")
    public ResponseEntity<String> avatar(
            @RequestParam @Size(min = 1, max = 80) String name,
            @RequestParam(required = false) @Min(32) @Max(1024) Integer size,
            @RequestParam(defaultValue = "CIRCLE") AvatarShape shape,
            @RequestParam(required = false) @Pattern(regexp = "^#[0-9a-fA-F]{6}$") String background,
            @RequestParam(required = false) @Pattern(regexp = "^#[0-9a-fA-F]{6}$") String color
    ) {
        SvgImage image = avatarService.generate(new AvatarRequest(name, size, shape, background, color));
        return svgResponse(image);
    }

    @Operation(summary = "Generate an SVG placeholder image")
    @GetMapping(value = "/placeholder", produces = "image/svg+xml")
    public ResponseEntity<String> placeholder(
            @RequestParam(required = false) @Min(64) @Max(2400) Integer width,
            @RequestParam(required = false) @Min(64) @Max(2400) Integer height,
            @RequestParam(required = false) @Size(max = 80) String label,
            @RequestParam(defaultValue = "#f1f5f9") @Pattern(regexp = "^#[0-9a-fA-F]{6}$") String background,
            @RequestParam(defaultValue = "#334155") @Pattern(regexp = "^#[0-9a-fA-F]{6}$") String color
    ) {
        SvgImage image = placeholderService.generate(new PlaceholderRequest(width, height, label, background, color));
        return svgResponse(image);
    }

    private ResponseEntity<String> svgResponse(SvgImage image) {
        return ResponseEntity.ok()
                .contentType(SVG_MEDIA_TYPE)
                .cacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic())
                .eTag(image.etag())
                .body(image.content());
    }
}
