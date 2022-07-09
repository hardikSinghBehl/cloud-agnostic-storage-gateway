package com.behl.strongbox.service.implementation;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.behl.strongbox.dto.FileRetrievalDto;
import com.behl.strongbox.dto.PresignedUrlResponseDto;

@Service
public class AzureStorageService {

	public HttpStatus save(final MultipartFile file) {
		return null;
	}

	public FileRetrievalDto retrieve(final String keyName) {
		return null;
	}

	public PresignedUrlResponseDto generatePresignedUrl(final String keyName) {
		return null;
	}

}
