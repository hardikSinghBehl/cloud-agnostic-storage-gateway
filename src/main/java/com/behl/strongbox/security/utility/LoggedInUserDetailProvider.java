package com.behl.strongbox.security.utility;

import java.util.UUID;

public class LoggedInUserDetailProvider {

	private static final ThreadLocal<UUID> userId = new ThreadLocal<UUID>();

	public static void setUserId(final UUID id) {
		userId.set(id);
	}

	public static UUID getId() {
		return userId.get();
	}

}