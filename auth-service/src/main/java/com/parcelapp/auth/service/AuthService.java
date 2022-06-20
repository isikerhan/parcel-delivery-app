package com.parcelapp.auth.service;

import com.parcelapp.auth.client.CloudFoundryUaaClient;
import com.parcelapp.auth.client.CloudFoundryUaaOAuthClient;
import com.parcelapp.auth.exception.GroupNotFoundException;
import com.parcelapp.auth.mapper.TokenMapper;
import com.parcelapp.auth.mapper.UserMapper;
import com.parcelapp.auth.model.AddMemberToGroupRequest;
import com.parcelapp.auth.model.CloudFoundryUaaAddMemberToGroupRequest;
import com.parcelapp.auth.model.CloudFoundryUaaCreateUserRequest;
import com.parcelapp.auth.model.CloudFoundryUaaGroupList;
import com.parcelapp.auth.model.CloudFoundryUaaToken;
import com.parcelapp.auth.model.CloudFoundryUaaTokenRequest;
import com.parcelapp.auth.model.CloudFoundryUaaUser;
import com.parcelapp.auth.model.CreateUserModel;
import com.parcelapp.auth.model.CredentialsModel;
import com.parcelapp.auth.model.TokenModel;
import com.parcelapp.auth.model.UserModel;
import com.parcelapp.auth.properties.IdentityServiceConfigurationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(IdentityServiceConfigurationProperties.class)
public class AuthService {
	static final String MEMBER_TYPE_USER = "USER";
	static final String ORIGIN_UAA = "uaa";
	static final String GRANT_TYPE_PASSWORD = "password";
	static final String TOKEN_FORMAT_JWT = "jwt";
	private final CloudFoundryUaaOAuthClient uaaOauthClient;
	private final CloudFoundryUaaClient uaaClient;
	private final TokenMapper tokenMapper;
	private final UserMapper userMapper;

	private final IdentityServiceConfigurationProperties identityServiceProps;

	public TokenModel login(CredentialsModel credentialsModel) {
		CloudFoundryUaaTokenRequest getTokenRequest = CloudFoundryUaaTokenRequest.builder()
				.clientId(identityServiceProps.getClientId())
				.clientSecret(identityServiceProps.getClientSecret())
				.grantType(GRANT_TYPE_PASSWORD)
				.username(credentialsModel.getUserName())
				.password(credentialsModel.getPassword())
				.tokenFormat(TOKEN_FORMAT_JWT)
				.build();

		CloudFoundryUaaToken token = uaaOauthClient.getToken(getTokenRequest);
		return tokenMapper.mapToToken(token);
	}

	public UserModel createUser(CreateUserModel createUserModel) {
		CloudFoundryUaaCreateUserRequest createUserRequest = userMapper.mapToUaaCreateUserRequest(createUserModel);
		CloudFoundryUaaUser user = uaaClient.createUser(createUserRequest);
		return userMapper.mapToUser(user);
	}

	public void addMemberToGroup(String userId, AddMemberToGroupRequest addMemberToGroupRequest) throws GroupNotFoundException {
		final String groupName = addMemberToGroupRequest.getGroup().getGroupName();

		final String filter = String.format("displayName eq '%s'", groupName);
		final CloudFoundryUaaGroupList groups = uaaClient.getGroups(filter);
		if (groups.getResources().isEmpty()) {
			throw new GroupNotFoundException();
		}
		final CloudFoundryUaaGroupList.CloudFoundryUaaGroup group = groups.getResources().get(0);
		final String groupId = group.getId();

		log.info("Adding user with id {} to group with id {}.", userId, groupId);

		CloudFoundryUaaAddMemberToGroupRequest cfUaaAddMemberToGroupRequest = CloudFoundryUaaAddMemberToGroupRequest.builder()
				.value(userId)
				.type(MEMBER_TYPE_USER)
				.origin(ORIGIN_UAA)
				.build();

		uaaClient.addMemberToGroup(groupId, cfUaaAddMemberToGroupRequest);
	}
}

