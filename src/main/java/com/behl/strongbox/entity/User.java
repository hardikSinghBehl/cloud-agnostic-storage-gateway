package com.behl.strongbox.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.behl.strongbox.constant.AccountType;

import lombok.Data;

@Data
@Document(collection = "users")
public class User implements Serializable {

	private static final long serialVersionUID = -6482424056597134764L;

	@Id
	@Field(name = "id")
	private UUID id;

	@NotNull
	@Indexed(unique = true)
	@Field(name = "user_name")
	private String userName;

	@NotNull
	@Field(name = "account_type")
	private AccountType accountType;

	@NotNull
	@Field(name = "password")
	private String password;

	@Field(name = "created_at")
	private LocalDateTime createdAt;

	@Field(name = "updated_at")
	private LocalDateTime updatedAt;

}