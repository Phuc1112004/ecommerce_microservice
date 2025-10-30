package org.example.catalog.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "djtrlira7",
                "api_key", "746351792961154",
                "api_secret", "20CF0cqbS3RtRAUmqkyA7W9jick",
                "secure", true
        ));
    }
}
