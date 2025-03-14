package com.deye.web.configuration;

import com.deye.web.service.impl.MinioConfigService;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MinioConfiguration {
    private final MinioConfigService minioConfigService;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .credentials(minioConfigService.getSecretKey(), minioConfigService.getAccessKey())
                .endpoint(minioConfigService.getUrl())
                .build();
    }
}
