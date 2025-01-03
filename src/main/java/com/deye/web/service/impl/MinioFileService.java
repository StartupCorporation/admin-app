package com.deye.web.service.impl;

import com.deye.web.exception.MinioException;
import com.deye.web.service.FileService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

import static com.deye.web.utils.error.ErrorCodeUtils.MINIO_DELETE_FILE_ERROR_CODE;
import static com.deye.web.utils.error.ErrorCodeUtils.MINIO_UPLOAD_FILE_ERROR_CODE;
import static com.deye.web.utils.error.ErrorMessageUtils.MINIO_DELETE_FILE_ERROR_MESSAGE;
import static com.deye.web.utils.error.ErrorMessageUtils.MINIO_UPLOAD_FILE_ERROR_MESSAGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioFileService implements FileService {
    private final ConfigService configService;
    private final MinioClient minio;

    /**
     * This method uploads image to the minio.
     *
     * @param file
     * @return file name saved in minio
     */
    public String upload(MultipartFile file) {
        try (InputStream content = file.getInputStream()) {
            String fileName = UUID.randomUUID() + "_" + file.getName();
            log.info("Generated filename: {}", fileName);
            uploadFile(content, fileName, file.getContentType(), file.getSize());
            log.info("Successfully uploaded image: {}", fileName);
            return fileName;
        } catch (Exception e) {
            log.error("Error occurred while uploading image file", e);
            throw new MinioException(MINIO_UPLOAD_FILE_ERROR_CODE, MINIO_UPLOAD_FILE_ERROR_MESSAGE);
        }
    }

    public void delete(String fileName) {
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                .bucket(configService.getMinioBucketName())
                .object(fileName)
                .build();
        try {
            minio.removeObject(removeObjectArgs);
        } catch (Exception e) {
            log.error("Error occurred while deleting image file", e);
            throw new MinioException(MINIO_DELETE_FILE_ERROR_CODE, MINIO_DELETE_FILE_ERROR_MESSAGE);
        }
    }

    private void uploadFile(InputStream content, String fileName, String contentType, Long imageSize) throws Exception {
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(configService.getMinioBucketName())
                .object(fileName)
                .contentType(contentType)
                .stream(content, imageSize, -1)
                .build();
        minio.putObject(putObjectArgs);
    }
}
