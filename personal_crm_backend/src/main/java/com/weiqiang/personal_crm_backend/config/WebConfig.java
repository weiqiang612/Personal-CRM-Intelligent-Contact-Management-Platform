package com.weiqiang.personal_crm_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Paths;

/**
 * Web MVC Configuration for static resources mapping
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.storage.upload-dir:./uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absolutePath = Paths.get(uploadDir).toAbsolutePath().toString();
        if (!absolutePath.endsWith(File.separator)) {
            absolutePath += File.separator;
        }
        String location = "file:" + absolutePath.replace("\\", "/");
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }
}
