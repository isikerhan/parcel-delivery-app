package com.parcelapp.courier.client;

import com.parcelapp.courier.model.AddMemberToGroupRequest;
import com.parcelapp.courier.model.CreateUserModel;
import com.parcelapp.courier.model.UserModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-client", url = "${urls.auth-service}", path = "/api")
public interface AuthClient {
	@PostMapping("/auth/users")
	UserModel createUser(@RequestBody CreateUserModel createUserModel);

	@PostMapping("/auth/users/{userId}/groups")
	void addMemberToGroup(@PathVariable String userId,
						  @RequestBody AddMemberToGroupRequest addMemberToGroupRequest);
}
