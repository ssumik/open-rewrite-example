package com.example.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.net.URI;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ImageControllerTest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void avatarReturnsSvgInitials() {
        ResponseEntity<String> response = rest.getForEntity(
                URI.create("/api/v1/avatar?name=Ada%20Lovelace&size=96&shape=SQUARE&background=%232563eb"),
                String.class
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(MediaType.valueOf("image/svg+xml"), response.getHeaders().getContentType());
        Assertions.assertNotNull(response.getHeaders().getETag());
        Assertions.assertTrue(response.getBody().contains(">AL</text>"));
        Assertions.assertTrue(response.getBody().contains("width=\"96\""));
    }

    @Test
    void placeholderReturnsSvgWithCustomLabel() {
        ResponseEntity<String> response = rest.getForEntity(
                "/api/v1/placeholder?width=320&height=180&label=Hero",
                String.class
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(response.getBody().contains(">Hero</text>"));
        Assertions.assertTrue(response.getBody().contains("viewBox=\"0 0 320 180\""));
    }

    @Test
    void invalidColorReturnsBadRequest() {
        ResponseEntity<String> response = rest.getForEntity(
                "/api/v1/avatar?name=Ada&background=blue",
                String.class
        );

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertTrue(response.getBody().contains("Request validation failed"));
    }
}
