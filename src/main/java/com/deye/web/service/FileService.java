package com.deye.web.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    /**
     * uploads file to the storage
     *
     * @param file - file, e.g image
     * @return file name
     */
    String upload(MultipartFile file);

    /**
     * deletes file from storage
     *
     * @param fileName key to find the file
     */
    void delete(String fileName);
}
