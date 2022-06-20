package com.parcelapp.auth.client;

import com.parcelapp.auth.model.CloudFoundryUaaAddMemberToGroupRequest;
import com.parcelapp.auth.model.CloudFoundryUaaCreateUserRequest;
import com.parcelapp.auth.model.CloudFoundryUaaGroupList;
import com.parcelapp.auth.model.CloudFoundryUaaUser;
import feign.RequestInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

@FeignClient(name = "uaa-client", url = "${urls.uaa}", path = "/uaa",
		configuration = CloudFoundryUaaClient.Configuration.class)
public interface CloudFoundryUaaClient {

	@PostMapping("/Users")
	CloudFoundryUaaUser createUser(@RequestBody CloudFoundryUaaCreateUserRequest request);

	@GetMapping("/Groups")
	CloudFoundryUaaGroupList getGroups(@RequestParam String filter);

	@PostMapping("/Groups/{groupId}/members")
	void addMemberToGroup(@PathVariable String groupId, @RequestBody CloudFoundryUaaAddMemberToGroupRequest request);

	class Configuration {
		@Bean
		RequestInterceptor requestInterceptor(OAuth2AuthorizedClientManager authorizedClientManager) {
			return template -> {
				if (!template.headers().containsKey("Authorization")) {
					var accessToken = getAccessToken(authorizedClientManager);
					if (Objects.nonNull(accessToken)) {
						template.header("Authorization", "Bearer " + accessToken.getTokenValue());
					}
				}
			};
		}

		private OAuth2AccessToken getAccessToken(OAuth2AuthorizedClientManager authorizedClientManager) {
			var request = OAuth2AuthorizeRequest
					.withClientRegistrationId("uaa")
					.principal(SecurityContextHolder.getContext().getAuthentication())
					.build();
			OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(request);
			return Objects.nonNull(authorizedClient) ? authorizedClient.getAccessToken() : null;
		}
	}
}
