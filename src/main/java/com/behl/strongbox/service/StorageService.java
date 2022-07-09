package com.behl.strongbox.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import com.behl.strongbox.dto.FileRetrievalDto;
import com.behl.strongbox.dto.PresignedUrlResponseDto;

public interface StorageService {

	HttpStatus save(MultipartFile file);

	FileRetrievalDto retrieve(String keyName);

	PresignedUrlResponseDto generatePresignedUrl(String keyName);

}