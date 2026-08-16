package com.example.demo.config;

import com.example.demo.config.properties.ImageApiProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ImageApiProperties.class)
public class ImageApiConfiguration {
}
