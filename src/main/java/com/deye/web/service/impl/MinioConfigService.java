package com.deye.web.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MinioConfigService {
    private final Environment env;

    public String getBucketName() {
        return env.getProperty("minio.bucket.name");
    }

    public String getSecretKey() {
        return env.getProperty("minio.secretKey");
    }

    public String getAccessKey() {
        return env.getProperty("minio.accessKey");
    }

    public String getUrl() {
        return env.getProperty("minio.url");
    }
}
