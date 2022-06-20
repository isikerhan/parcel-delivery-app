package com.parcelapp.order.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class FeignTokenRelayRequestInterceptor implements RequestInterceptor {

	private final HttpServletRequest request;

	@Override
	public void apply(RequestTemplate template) {
		final String authorizationHeader = request.getHeader(AUTHORIZATION);
		if (Objects.nonNull(authorizationHeader) && !template.headers().containsKey(AUTHORIZATION)) {
			template.header(AUTHORIZATION, authorizationHeader);
		}
	}
}
