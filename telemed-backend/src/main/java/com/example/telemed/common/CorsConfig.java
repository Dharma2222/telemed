package com.example.telemed.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        // For dev, you can allow all. Prefer setting explicit origins listed below.
        .allowedOriginPatterns("*")
        .allowedMethods("GET","POST","PUT","DELETE","PATCH","OPTIONS")
        // Must allow custom headers used by your app:
        .allowedHeaders("Content-Type","Idempotency-Key","Accept","Origin","Authorization")
        // Expose any headers you want the browser to read:
        .exposedHeaders("Location")
        .allowCredentials(false)   // keep false when using '*'
        .maxAge(3600);
  }
}
