package com.behl.strongbox.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.behl.strongbox.constant.Platform;
import com.mongodb.lang.Nullable;

import lombok.Data;

@Data
@Document(collection = "file_details")
public class FileDetail implements Serializable {

	private static final long serialVersionUID = 1797840908775772311L;

	@Id
	@Field(name = "id")
	private UUID id;

	@NotNull
	@Field(name = "platform")
	private Platform platform;

	@NotNull
	@Field(name = "bucket_name")
	private String bucketName;

	@NotNull
	@Field(name = "content_disposition")
	private String contentDisposition;

	@NotNull
	@Field(name = "content_type")
	private String contentType;

	@NotNull
	@Field(name = "content_size")
	private long contentSize;

	@Nullable
	@Field(name = "custom_metadata")
	private Map<String, Object> customMetadata;

	@NotNull
	@Field(name = "created_by")
	private UUID createdBy;

	@NotNull
	@Field(name = "updated_by")
	private UUID updatedBy;

	@NotNull
	@Field(name = "created_at")
	private LocalDateTime createdAt;

	@NotNull
	@Field(name = "updated_at")
	private LocalDateTime updatedAt;

}
