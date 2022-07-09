package com.behl.strongbox.service.implementation;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.behl.strongbox.dto.FileRetrievalDto;
import com.behl.strongbox.dto.PresignedUrlResponseDto;
import com.behl.strongbox.service.StorageService;

@Service
public class AzureStorageService implements StorageService {

	@Override
	public HttpStatus save(final MultipartFile file) {
		return null;
	}

	@Override
	public FileRetrievalDto retrieve(final String keyName) {
		return null;
	}

	@Override
	public PresignedUrlResponseDto generatePresignedUrl(final String keyName) {
		return null;
	}

}
