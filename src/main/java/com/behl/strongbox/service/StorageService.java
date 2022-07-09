package com.behl.strongbox.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import com.behl.strongbox.constant.Platform;
import com.behl.strongbox.dto.FileRetrievalDto;
import com.behl.strongbox.dto.PresignedUrlResponseDto;

import lombok.NonNull;

public interface StorageService {

	HttpStatus save(@NonNull Platform platform, @NonNull MultipartFile file);

	FileRetrievalDto retrieve(@NonNull Platform platform, @NonNull String keyName);

	PresignedUrlResponseDto generatePresignedUrl(@NonNull Platform platform, @NonNull String keyName);

}