package com.parcelapp.auth.mapper;

import com.parcelapp.auth.model.CloudFoundryUaaToken;
import com.parcelapp.auth.model.TokenModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TokenMapper {
	TokenModel mapToToken(CloudFoundryUaaToken uaaToken);
}
