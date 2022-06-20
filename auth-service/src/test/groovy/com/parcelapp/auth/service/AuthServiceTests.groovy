package com.parcelapp.auth.service

import com.parcelapp.auth.client.CloudFoundryUaaClient
import com.parcelapp.auth.client.CloudFoundryUaaOAuthClient
import com.parcelapp.auth.mapper.TokenMapper
import com.parcelapp.auth.mapper.UserMapper
import com.parcelapp.auth.model.AddMemberToGroupRequest
import com.parcelapp.auth.model.CloudFoundryUaaAddMemberToGroupRequest
import com.parcelapp.auth.model.CloudFoundryUaaCreateUserRequest
import com.parcelapp.auth.model.CloudFoundryUaaGroupList
import com.parcelapp.auth.model.CloudFoundryUaaToken
import com.parcelapp.auth.model.CloudFoundryUaaTokenRequest
import com.parcelapp.auth.model.CloudFoundryUaaUser
import com.parcelapp.auth.model.CreateUserModel
import com.parcelapp.auth.model.CredentialsModel
import com.parcelapp.auth.properties.IdentityServiceConfigurationProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import static com.parcelapp.auth.model.UserGroup.COURIER
import static com.parcelapp.auth.service.AuthService.GRANT_TYPE_PASSWORD
import static com.parcelapp.auth.service.AuthService.MEMBER_TYPE_USER
import static com.parcelapp.auth.service.AuthService.ORIGIN_UAA
import static com.parcelapp.auth.service.AuthService.TOKEN_FORMAT_JWT

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTests extends Specification {

    private AuthService authService

    @Autowired
    private TokenMapper tokenMapper
    @Autowired
    private UserMapper userMapper
    @Autowired
    private IdentityServiceConfigurationProperties identityServiceProps

    private CloudFoundryUaaOAuthClient uaaOauthClient
    private CloudFoundryUaaClient uaaClient

    void setup() {
        uaaOauthClient = Mock(CloudFoundryUaaOAuthClient.class)
        uaaClient = Mock(CloudFoundryUaaClient.class)

        authService = new AuthService(uaaOauthClient, uaaClient, tokenMapper, userMapper, identityServiceProps)
    }

    def "login"() {
        given: "credentials"
        def tokenRequest = new CredentialsModel()
        tokenRequest.userName = "test_user"
        tokenRequest.password = "pass"

        and: "UAA token request"
        def uaaTokenRequest = CloudFoundryUaaTokenRequest.builder()
                .clientId(identityServiceProps.getClientId())
                .clientSecret(identityServiceProps.getClientSecret())
                .grantType(GRANT_TYPE_PASSWORD)
                .username(tokenRequest.userName)
                .password(tokenRequest.password)
                .tokenFormat(TOKEN_FORMAT_JWT)
                .build()

        and: "UAA token"
        def uaaToken = new CloudFoundryUaaToken()
        uaaToken.accessToken = "the_access_token"
        uaaToken.idToken = "the_id_token"
        uaaToken.tokenType = "bearer"
        uaaToken.expiresIn = 3600L
        uaaToken.jti = "jti"
        uaaToken.scope = "scope"

        and:
        "UAA oauth client returning the access token " +
                "if the credentials are ${tokenRequest.userName} / ${tokenRequest.password}"
        uaaOauthClient.getToken(uaaTokenRequest) >> uaaToken

        when: "getting access token"
        def token = authService.login(tokenRequest)

        then: "got the access token"
        1 * uaaOauthClient.getToken(uaaTokenRequest) >> uaaToken

        and:
        token
        token.accessToken == "the_access_token"
        token.idToken == "the_id_token"
        token.tokenType == "bearer"
        token.expiresIn == 3600L
    }

    def "create user"() {
        given: "user info to be created"
        def createUserModel = new CreateUserModel()
        createUserModel.userName = "test_user"
        createUserModel.password = "pass"
        createUserModel.emailAddress = "test@example.org"
        createUserModel.firstName = "Test"
        createUserModel.lastName = "User"

        and: "create user request"
        def createUserRequest = new CloudFoundryUaaCreateUserRequest()
        createUserRequest.userName = "test_user"
        createUserRequest.password = "pass"
        createUserRequest.name = CloudFoundryUaaUser.Name.builder()
                .givenName("Test").familyName("User")
                .formatted("Test User").build()
        createUserRequest.emails = List.of(CloudFoundryUaaUser.Email.builder()
                .primary(true).value("test@example.org").build())

        and: "resulting user"
        def user = new CloudFoundryUaaUser()
        user.id = "some_user_id"
        user.userName = "test_user"
        user.name = CloudFoundryUaaUser.Name.builder()
                .givenName("Test").familyName("User")
                .formatted("Test User").build()
        user.emails = List.of(CloudFoundryUaaUser.Email.builder()
                .primary(true).value("test@example.org").build())

        and: "UAA client returning our user"
        uaaClient.createUser(createUserRequest) >> user

        when: "creating user"
        def createdUser = authService.createUser(createUserModel)

        then: "create user"
        1 * uaaClient.createUser(createUserRequest) >> user

        and:
        createdUser
        createdUser.id == "some_user_id"
        createdUser.userName == "test_user"
        createdUser.emailAddress == "test@example.org"
        createdUser.firstName == "Test"
        createdUser.lastName == "User"
    }

    def "add member to group"() {
        given: "a user id"
        def userId = "some_user_id"

        and: "group info"
        def groupInfo = new AddMemberToGroupRequest()
        groupInfo.group = COURIER

        and: "add member"
        def addMemberToGroupRequest = CloudFoundryUaaAddMemberToGroupRequest.builder()
                .value("some_user_id")
                .type(MEMBER_TYPE_USER)
                .origin(ORIGIN_UAA)
                .build()
        and: "group id"
        def groupId = "some_group_id"

        and: "groups response"
        def groupsResponse = new CloudFoundryUaaGroupList()
        def group = new CloudFoundryUaaGroupList.CloudFoundryUaaGroup()
        group.displayName = COURIER.groupName
        group.id = groupId
        groupsResponse.resources = List.of(group)

        and: "UAA client returning the group list"
        uaaClient.getGroups("displayName eq 'some_group_id'") >> groupsResponse

        and: "UAA client adding the member without errors"
        uaaClient.addMemberToGroup(groupId, addMemberToGroupRequest)

        when: "adding user to the group"
        authService.addMemberToGroup(userId, groupInfo)

        then: "successfully add user"
        noExceptionThrown()

        and:
        1 * uaaClient.getGroups("displayName eq '${COURIER.groupName}'") >> groupsResponse

        and:
        1 * uaaClient.addMemberToGroup(groupId, addMemberToGroupRequest)
    }
}
