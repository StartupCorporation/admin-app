package com.deye.web.utils.error;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorCodeUtils {
    public static final Integer MINIO_UPLOAD_FILE_ERROR_CODE = 1;
    public static final Integer MINIO_DELETE_FILE_ERROR_CODE = 2;
    public static final Integer CATEGORY_NOT_FOUND_ERROR_CODE = 3;
    public static final Integer REQUEST_BODY_VALIDATION_ERROR_CODE = 4;
    public static final Integer DATABASE_ERROR_CODE = 5;
}
