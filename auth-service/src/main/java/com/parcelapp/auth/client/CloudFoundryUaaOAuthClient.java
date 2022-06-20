package com.parcelapp.auth.client;

import com.parcelapp.auth.model.CloudFoundryUaaToken;
import com.parcelapp.auth.model.CloudFoundryUaaTokenRequest;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@FeignClient(name = "uaa-oauth-client", url = "${urls.uaa}", path = "/uaa",
		configuration = CloudFoundryUaaOAuthClient.Configuration.class)
public interface CloudFoundryUaaOAuthClient {

	@PostMapping(value = "/oauth/token", consumes = APPLICATION_FORM_URLENCODED_VALUE)
	CloudFoundryUaaToken getToken(CloudFoundryUaaTokenRequest request);

	class Configuration {
		@Bean
		Encoder feignFormEncoder(ObjectFactory<HttpMessageConverters> converters) {
			return new SpringFormEncoder(new SpringEncoder(converters));
		}
	}
}
