package com.deye.web.service.impl;

import com.deye.web.exception.FileStorageException;
import com.deye.web.service.FileService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

import static com.deye.web.util.error.ErrorCodeUtils.MINIO_DELETE_FILE_ERROR_CODE;
import static com.deye.web.util.error.ErrorCodeUtils.MINIO_UPLOAD_FILE_ERROR_CODE;
import static com.deye.web.util.error.ErrorMessageUtils.MINIO_DELETE_FILE_ERROR_MESSAGE;
import static com.deye.web.util.error.ErrorMessageUtils.MINIO_UPLOAD_FILE_ERROR_MESSAGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioFileService implements FileService {
    private final MinioConfigService minioConfigService;
    private final MinioClient minio;

    public void upload(MultipartFile file) {
        try (InputStream content = file.getInputStream()) {
            String bucketName = minioConfigService.getBucketName();
            String fileName = file.getOriginalFilename();
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .contentType(file.getContentType())
                    .stream(content, file.getSize(), -1)
                    .build();
            minio.putObject(putObjectArgs);
            log.info("Successfully uploaded image: {}", fileName);
        } catch (Exception e) {
            log.error("Error occurred while uploading image file", e);
            throw new FileStorageException(MINIO_UPLOAD_FILE_ERROR_CODE, MINIO_UPLOAD_FILE_ERROR_MESSAGE);
        }
    }

    public void delete(String fileName) {
        String bucketName = minioConfigService.getBucketName();
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .build();
        try {
            minio.removeObject(removeObjectArgs);
        } catch (Exception e) {
            log.error("Error occurred while deleting image file", e);
            throw new FileStorageException(MINIO_DELETE_FILE_ERROR_CODE, MINIO_DELETE_FILE_ERROR_MESSAGE);
        }
    }
}
