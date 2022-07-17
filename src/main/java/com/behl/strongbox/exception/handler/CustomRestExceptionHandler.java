package com.behl.strongbox.exception.handler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.behl.strongbox.exception.dto.ExceptionResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestControllerAdvice
@RequiredArgsConstructor
@Log4j2
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

	@ResponseBody
	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<?> responseStatusExceptionHandler(final ResponseStatusException exception) {
		log.error("Exception {} thrown: {}", exception.getClass().getName(), LocalDateTime.now(), exception);
		return ResponseEntity.status(exception.getStatus())
				.body(ExceptionResponseDto.builder().status(exception.getStatus().value())
						.message(exception.getReason()).description(exception.getMessage())
						.timestamp(LocalDateTime.now()).build());
	}

	@ResponseBody
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException exception,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		BindingResult result = exception.getBindingResult();
		List<FieldError> fieldErrors = result.getFieldErrors();

		final var response = new HashMap<String, Object>();
		response.put("status", "Failure");
		response.put("message",
				fieldErrors.stream().map(fieldError -> fieldError.getDefaultMessage()).collect(Collectors.toList()));
		response.put("timestamp",
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toString());

		return ResponseEntity.badRequest().body(response);
	}

	@ResponseBody
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> serverExceptionHandler(final Exception exception) {
		log.error("Exception {} thrown: {}", exception.getClass().getName(), LocalDateTime.now(), exception);
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
				.body(ExceptionResponseDto.builder().status(501).message("Exception Occurred")
						.description(exception.getMessage()).timestamp(LocalDateTime.now()).build());
	}

}