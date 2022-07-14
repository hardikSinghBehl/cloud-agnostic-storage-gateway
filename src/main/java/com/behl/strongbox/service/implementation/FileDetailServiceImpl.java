package com.behl.strongbox.service.implementation;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.behl.strongbox.constant.Platform;
import com.behl.strongbox.entity.FileDetail;
import com.behl.strongbox.repository.FileDetailRepository;
import com.behl.strongbox.security.utility.LoggedInUserDetailProvider;
import com.behl.strongbox.service.FileDetailService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileDetailServiceImpl implements FileDetailService {

	private final FileDetailRepository fileDetailRepository;

	@Override
	public UUID save(@NonNull final MultipartFile file, @Nullable Map<String, Object> customMetadata,
			@NonNull final Platform platform, @NonNull final String bucketName) {
		final var currentTimestamp = LocalDateTime.now(ZoneOffset.UTC);
		final var fileDetail = new FileDetail();
		final var loggedInUser = LoggedInUserDetailProvider.getId();
		fileDetail.setId(UUID.randomUUID());
		fileDetail.setPlatform(platform);
		fileDetail.setBucketName(bucketName);
		fileDetail.setContentDisposition(file.getOriginalFilename());
		fileDetail.setContentType(file.getContentType());
		fileDetail.setContentSize(file.getSize());
		fileDetail.setCustomMetadata(customMetadata);
		fileDetail.setCreatedBy(loggedInUser);
		fileDetail.setUpdatedBy(loggedInUser);
		fileDetail.setCreatedAt(currentTimestamp);
		fileDetail.setUpdatedAt(currentTimestamp);

		final var savedFileDetail = fileDetailRepository.save(fileDetail);
		log.info("Successfully saved file details for '{}' : action initiated by '{}' : {}", file.getOriginalFilename(),
				loggedInUser, currentTimestamp);
		return savedFileDetail.getId();
	}

	@Override
	public FileDetail getById(@NonNull final UUID referenceId) {
		return fileDetailRepository.findById(referenceId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid referenceId provided"));
	}

}
