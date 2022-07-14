package com.behl.strongbox.service;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.behl.strongbox.constant.Platform;
import com.behl.strongbox.entity.FileDetail;

import lombok.NonNull;

public interface FileDetailService {

	UUID save(@NonNull final MultipartFile file, @NonNull final Platform platform, @NonNull final String bucketName);

	FileDetail getById(@NonNull final UUID referenceId);

}
