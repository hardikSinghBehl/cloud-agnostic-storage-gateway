package com.behl.strongbox.service;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.behl.strongbox.constant.Platform;

import lombok.NonNull;

public interface FileDetailService {

	UUID save(@NonNull final MultipartFile file, @NonNull final Platform platform, @NonNull final String bucketName);

}
