package com.example.demo.model;

public record AvatarRequest(
        String name,
        Integer size,
        AvatarShape shape,
        String background,
        String color
) {
    public AvatarRequest {
        name = name.strip();
        shape = shape == null ? AvatarShape.CIRCLE : shape;
    }
}
