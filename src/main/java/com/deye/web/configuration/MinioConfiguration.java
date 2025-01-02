package com.deye.web.configuration;

import com.deye.web.service.impl.ConfigService;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MinioConfiguration {
    private final ConfigService configService;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .credentials(configService.getMinioSecretKey(), configService.getMinioAccessKey())
                .endpoint(configService.getMinioUrl())
                .build();
    }
}
