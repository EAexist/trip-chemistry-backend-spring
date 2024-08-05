package com.example.tripchemistry.configuration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://localhost:5173",
                     "https://trip-chemistry.vercel.app", 
                     "https://trip-chemistry-remix.vercel.app", 
                     "https://trip-chemistry-git-1-1-eaexists-projects.vercel.app/",
                     "http://localhost:3000", 
                     "http://192.168.0.22:3000"
                )
                .allowedMethods("*");
                // .allowedMethods("GET", "POST", "PUT", "DELETE")
                // .allowedHeaders("Authorization", "Content-Type")
                // .exposedHeaders("Custom-Header")
                // .allowCredentials(true)
                // .maxAge(3600);
    }
}