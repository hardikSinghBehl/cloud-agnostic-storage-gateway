package com.behl.strongbox.aspect;

import java.time.LocalDateTime;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.behl.strongbox.annotation.CheckIfAuthorizedUser;
import com.behl.strongbox.constant.AccountType;
import com.behl.strongbox.repository.UserRepository;
import com.behl.strongbox.security.utility.JwtUtility;
import com.behl.strongbox.utility.ArgumentUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class CheckIfAuthorizedUserAspect {

	private final UserRepository userRepository;
	private final JwtUtility jwtUtility;

	@Before("execution(* *.*(..)) && @annotation(checkIfAuthorizedUser)")
	public void checkIfAuthorizedUser(final JoinPoint joinPoint, final CheckIfAuthorizedUser checkIfAuthorizedUser) {
		log.info("RUNNING AUTHORIZED_USER VALIDATION ON METHOD {}() in {}.class : {}",
				joinPoint.getSignature().getName(), joinPoint.getSignature().getDeclaringType().getSimpleName(),
				LocalDateTime.now());

		final var header = ArgumentUtil.getJwt(joinPoint);
		final var userId = jwtUtility.extractUserId(header);
		final var user = userRepository.findById(userId).get();
		if (AccountType.GUEST_USER.equals(user.getAccountType()))
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
					"NEED TO BE AN AUTHORIZED USER TO INVOKE THIS ENDPOINT");
	}

}
