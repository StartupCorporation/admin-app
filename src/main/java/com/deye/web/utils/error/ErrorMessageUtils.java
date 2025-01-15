package com.deye.web.utils.error;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorMessageUtils {
    public static final String MINIO_UPLOAD_FILE_ERROR_MESSAGE = "Error during upload file to MinIO";
    public static final String MINIO_DELETE_FILE_ERROR_MESSAGE = "Error during deleting file in MinIO";
    public static final String CATEGORY_NOT_FOUND_ERROR_MESSAGE = "Category not found";
    public static final String EVENT_MESSAGE_NOT_PROCEED_ERROR_MESSAGE = "Error during event message processing";
}
