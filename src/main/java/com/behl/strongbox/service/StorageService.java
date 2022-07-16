package com.behl.strongbox.service;

import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.springframework.web.multipart.MultipartFile;

import com.behl.strongbox.constant.Platform;
import com.behl.strongbox.dto.FileRetrievalDto;
import com.behl.strongbox.dto.FileStorageSuccessDto;
import com.behl.strongbox.dto.PresignedUrlResponseDto;

import lombok.NonNull;

public interface StorageService {

	FileStorageSuccessDto save(@NonNull Platform platform, @NonNull MultipartFile file,
			@Nullable Map<String, Object> customMetadata);

	FileRetrievalDto retrieve(@NonNull UUID referenceId);

	PresignedUrlResponseDto generatePresignedUrl(@NonNull UUID keyName);

}