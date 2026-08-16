package com.example.demo.model;

public record PlaceholderRequest(
        Integer width,
        Integer height,
        String label,
        String background,
        String color
) {
    public PlaceholderRequest {
        label = label == null || label.isBlank() ? null : label.strip();
    }
}
