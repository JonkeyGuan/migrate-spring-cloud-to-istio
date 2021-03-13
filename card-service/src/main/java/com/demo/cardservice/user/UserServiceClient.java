package com.demo.cardservice.user;

import com.demo.cardservice.application.CardApplicationDto;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserServiceClient {

	private final RestTemplate restTemplate;

	@Value("${userservice.base.url}")
    private String userServiceBaseUrl;

	UserServiceClient(@Qualifier("restTemplate") RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public ResponseEntity<User> registerUser(CardApplicationDto.User userDto) {
		return restTemplate.postForEntity(userServiceBaseUrl + "/registration", userDto,
				User.class);
	}
}