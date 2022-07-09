package com.behl.strongbox.service;

import com.behl.strongbox.dto.RefreshTokenRequestDto;
import com.behl.strongbox.dto.TokenSuccessResponseDto;
import com.behl.strongbox.dto.UserAuthorizationRequestDto;

import lombok.NonNull;

public interface AuthenticationService {

	TokenSuccessResponseDto login(@NonNull UserAuthorizationRequestDto userAuthorizationRequestDto);

	TokenSuccessResponseDto refreshToken(@NonNull RefreshTokenRequestDto refreshTokenRequestDto);

}