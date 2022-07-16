package com.behl.strongbox.utility;

import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.ObjectMetadata;

public class StorageUtility {

	private StorageUtility() {
	}

	public static ObjectMetadata constructMetadata(final MultipartFile file) {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(file.getSize());
		metadata.setContentType(file.getContentType());
		metadata.setContentDisposition(file.getOriginalFilename());
		return metadata;
	}

}
