package ru.example.otc.config;

import io.minio.MinioClient;

public final class MinioClientConfig {

    private MinioClientConfig() {
    }

    public static MinioClient createClient() {
        return MinioClient.builder()
                .endpoint(
                        TestConfig.s3Endpoint()
                )
                .credentials(
                        TestConfig.minioRootUser(),
                        TestConfig.minioRootPassword()
                )
                .build();
    }
}