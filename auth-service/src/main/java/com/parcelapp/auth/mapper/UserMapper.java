package com.parcelapp.auth.mapper;

import com.parcelapp.auth.model.CloudFoundryUaaCreateUserRequest;
import com.parcelapp.auth.model.CloudFoundryUaaUser;
import com.parcelapp.auth.model.CreateUserModel;
import com.parcelapp.auth.model.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
	@Mapping(expression = "java(uaaUser.getEmails().isEmpty() ? null : uaaUser.getEmails().get(0).getValue())",
			target = "emailAddress")
	@Mapping(source = "name.givenName", target = "firstName")
	@Mapping(source = "name.familyName", target = "lastName")
	UserModel mapToUser(CloudFoundryUaaUser uaaUser);

	@Mapping(expression = "java(java.util.Arrays.asList(new CloudFoundryUaaUser.Email(createUserModel.getEmailAddress(), true)))",
			target = "emails")
	@Mapping(expression = "java(String.join(\" \", createUserModel.getFirstName(), createUserModel.getLastName()))",
			target = "name.formatted")
	@Mapping(source = "firstName", target = "name.givenName")
	@Mapping(source = "lastName", target = "name.familyName")
	CloudFoundryUaaCreateUserRequest mapToUaaCreateUserRequest(CreateUserModel createUserModel);
}
