package com.example.tripchemistry.configuration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://trip-chemistry-0-1.vercel.app/", "http://localhost:5173", "http://localhost:3000", "https://trip-chemistry.vercel.app" )
                .allowedMethods("*");
                // .allowedMethods("GET", "POST", "PUT", "DELETE")
                // .allowedHeaders("Authorization", "Content-Type")
                // .exposedHeaders("Custom-Header")
                // .allowCredentials(true)
                // .maxAge(3600);
    }
}