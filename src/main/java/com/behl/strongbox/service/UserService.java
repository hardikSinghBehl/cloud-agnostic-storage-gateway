package com.behl.strongbox.service;

import com.behl.strongbox.dto.UserCreationRequestDto;

import lombok.NonNull;

public interface UserService {

	void create(@NonNull UserCreationRequestDto userCreationRequestDto);

}