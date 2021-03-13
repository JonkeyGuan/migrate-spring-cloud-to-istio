package com.demo.cardservice.verification;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class VerificationServiceClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerificationServiceClient.class);
	private final RestTemplate restTemplate;

	@Value("${fraudverify.base.url}")
    private String fraudVerifyBaseUrl;

	VerificationServiceClient(@LoadBalanced RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		this.restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
	}

	public ResponseEntity<VerificationResult> verify(VerificationApplication verificationApplication) {
		LOGGER.debug("Sending verification request for application placed by user {}",
				verificationApplication.getUserId());
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
				.fromHttpUrl(fraudVerifyBaseUrl + "/cards/verify")
				.queryParam("uuid", verificationApplication.getUserId())
				.queryParam("cardCapacity", verificationApplication.getCardCapacity());
		try {
			return restTemplate.getForEntity(uriComponentsBuilder.toUriString(), VerificationResult.class);
		} catch (Throwable e) {
        	return userRejected(verificationApplication.getUserId());
        }
		
	}

    private ResponseEntity<VerificationResult> userRejected(UUID userUuid) {
        return new ResponseEntity<>(VerificationResult.failed(userUuid), HttpStatus.SERVICE_UNAVAILABLE);
    }
}
