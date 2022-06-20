package com.parcelapp.order.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@NoArgsConstructor(access = AccessLevel.NONE)
public class AuthUtils {
	public static boolean hasAuthority(JwtAuthenticationToken jwtAuthenticationToken, String authority) {
		return jwtAuthenticationToken.getAuthorities()
				.stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority));
	}
}
