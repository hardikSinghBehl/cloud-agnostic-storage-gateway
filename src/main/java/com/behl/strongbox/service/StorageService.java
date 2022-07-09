package com.behl.strongbox.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import com.behl.strongbox.dto.FileRetrievalDto;

public interface StorageService {

	HttpStatus save(MultipartFile file);

	FileRetrievalDto retrieve(String keyName);

}