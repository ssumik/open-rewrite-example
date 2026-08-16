package com.example.demo.service;

import org.springframework.stereotype.Component;

@Component
public class SvgSanitizer {

    public String escapeText(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    public String escapeAttribute(String value) {
        return escapeText(value)
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
