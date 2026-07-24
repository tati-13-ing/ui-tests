package ru.example.otc.service;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.UploadObjectArgs;
import io.minio.errors.ErrorResponseException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

public class ProductS3StorageService {

    private final MinioClient minioClient;
    private final String bucketName;
    private final String objectName;

    public ProductS3StorageService(
            MinioClient minioClient,
            String bucketName,
            String objectName
    ) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
        this.objectName = objectName;
    }

    public void uploadFile(
            Path filePath
    ) throws Exception {
        createBucketIfMissing();

        minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .filename(
                                filePath
                                        .toAbsolutePath()
                                        .toString()
                        )
                        .contentType(
                                "text/plain; charset=UTF-8"
                        )
                        .build()
        );
    }

    public boolean objectExists()
            throws Exception {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );

            return true;
        } catch (ErrorResponseException exception) {
            String errorCode =
                    exception
                            .errorResponse()
                            .code();

            if ("NoSuchKey".equals(errorCode)
                    || "NoSuchObject".equals(errorCode)) {

                return false;
            }

            throw exception;
        }
    }

    public List<String> readLines()
            throws Exception {
        try (
                GetObjectResponse response =
                        minioClient.getObject(
                                GetObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(objectName)
                                        .build()
                        );

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        response,
                                        StandardCharsets.UTF_8
                                )
                        )
        ) {
            return reader
                    .lines()
                    .toList();
        }
    }

    private void createBucketIfMissing()
            throws Exception {
        boolean bucketExists =
                minioClient.bucketExists(
                        BucketExistsArgs.builder()
                                .bucket(bucketName)
                                .build()
                );

        if (!bucketExists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
        }
    }
}