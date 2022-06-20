package com.parcelapp.auth.controller;

import com.parcelapp.auth.exception.GroupNotFoundException;
import com.parcelapp.auth.model.AddMemberToGroupRequest;
import com.parcelapp.auth.model.CreateUserModel;
import com.parcelapp.auth.model.CredentialsModel;
import com.parcelapp.auth.model.ErrorResponse;
import com.parcelapp.auth.model.TokenModel;
import com.parcelapp.auth.model.UserModel;
import com.parcelapp.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/auth")
@RequiredArgsConstructor
@Tag(name = "Auth Controller", description = "Operations related to authentication and user management.")
public class AuthController {

	private final AuthService authService;

	@PostMapping(path = "/token", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Authenticate with user name and password and get access token.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Authenticated successfully."),
			@ApiResponse(responseCode = "401", description = "Invalid credentials supplied.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public TokenModel login(@Valid @RequestBody CredentialsModel credentialsModel) {
		return authService.login(credentialsModel);
	}

	@PostMapping(path = "/users", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Create a user.")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "User created successfully."),
			@ApiResponse(responseCode = "400", description = "Invalid user info supplied.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public UserModel createUser(@Valid @RequestBody CreateUserModel createUserModel) {
		return authService.createUser(createUserModel);
	}

	@PreAuthorize("hasAuthority('SCOPE_parcel_app.admin')")
	@PostMapping(path = "/users/{userId}/groups", consumes = APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Add user to group as a member.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "User membership in the group created successfully."),
			@ApiResponse(responseCode = "400", description = "Invalid group info supplied.",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@SecurityRequirement(name = "bearerAuth")
	public void addMemberToGroup(
			@Parameter(description = "User id to add to the group.") @PathVariable String userId,
			@Valid @RequestBody AddMemberToGroupRequest addMemberToGroupRequest) throws GroupNotFoundException {
		authService.addMemberToGroup(userId, addMemberToGroupRequest);
	}
}
