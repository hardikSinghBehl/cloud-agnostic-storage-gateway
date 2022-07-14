package com.behl.strongbox.service.implementation;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.behl.strongbox.dto.UserCreationRequestDto;
import com.behl.strongbox.entity.User;
import com.behl.strongbox.repository.UserRepository;
import com.behl.strongbox.service.UserService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void create(@NonNull final UserCreationRequestDto userCreationRequestDto) {
		if (userNameAlreadyTaken(userCreationRequestDto.getUserName()))
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Account with provided userName already exists");

		final var user = new User();
		user.setId(UUID.randomUUID());
		user.setUserName(userCreationRequestDto.getUserName());
		user.setAccountType(userCreationRequestDto.getAccountType());
		user.setPassword(passwordEncoder.encode(userCreationRequestDto.getPassword()));
		user.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
		user.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
		userRepository.save(user);
	}

	private boolean userNameAlreadyTaken(@NonNull final String userName) {
		return userRepository.existsByUserName(userName);
	}

}