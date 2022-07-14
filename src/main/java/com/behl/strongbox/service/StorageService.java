package com.behl.strongbox.service;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.behl.strongbox.constant.Platform;
import com.behl.strongbox.dto.FileRetrievalDto;
import com.behl.strongbox.dto.FileStorageSuccessDto;
import com.behl.strongbox.dto.PresignedUrlResponseDto;

import lombok.NonNull;

public interface StorageService {

	FileStorageSuccessDto save(@NonNull Platform platform, @NonNull MultipartFile file);

	FileRetrievalDto retrieve(@NonNull UUID referenceId);

	PresignedUrlResponseDto generatePresignedUrl(@NonNull UUID keyName);

}