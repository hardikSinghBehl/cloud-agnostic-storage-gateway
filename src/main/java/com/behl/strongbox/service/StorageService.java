package com.behl.strongbox.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import com.behl.strongbox.dto.FileRetrievalDto;
import com.behl.strongbox.dto.PresignedUrlResponseDto;

import lombok.NonNull;

public interface StorageService {

	HttpStatus save(@NonNull MultipartFile file);

	FileRetrievalDto retrieve(@NonNull String keyName);

	PresignedUrlResponseDto generatePresignedUrl(@NonNull String keyName);

}