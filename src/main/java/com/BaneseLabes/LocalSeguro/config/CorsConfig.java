package com.BaneseLabes.LocalSeguro.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600); // Cache preflight response for 1 hour
        System.out.println("CORS configuration applied for /safetyPlace/**");
    }
}

    //@Configuration
    //public class CorsConfig implements WebMvcConfigurer {
    //    @Override
    //    public void addCorsMappings(CorsRegistry registry) {
    //        registry.addMapping("/**")
    //                .allowedOrigins("*") // Add production frontend URL if needed
    //                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT")
    //                .allowedHeaders("*")
    //                .allowCredentials(true)
    //                .maxAge(3600); // Cache preflight response for 1 hour
    //        System.out.println("CORS configuration applied for /safetyPlace/**");
    //    }
    //}
