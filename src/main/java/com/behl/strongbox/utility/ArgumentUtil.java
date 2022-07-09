package com.behl.strongbox.utility;

import java.util.concurrent.atomic.AtomicInteger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;

public class ArgumentUtil {

	public static String getJwt(final JoinPoint joinPoint) {
		final var codeSignature = (CodeSignature) joinPoint.getSignature();
		Integer headerArgumentPosition = null;
		final AtomicInteger count = new AtomicInteger(0);

		for (int i = 0; i < codeSignature.getParameterNames().length; i++) {
			if (codeSignature.getParameterNames()[i].equalsIgnoreCase("accessToken")) {
				headerArgumentPosition = count.get();
				break;
			} else
				count.incrementAndGet();
		}

		return (String) joinPoint.getArgs()[headerArgumentPosition];
	}

}
