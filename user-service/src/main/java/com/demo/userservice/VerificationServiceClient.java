
package com.demo.userservice;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Component
public class VerificationServiceClient {

    private final RestTemplate restTemplate;
    private final Timer verifyNewUserTimer;

    @Value("${fraudverify.base.url}")
    private String fraudVerifyBaseUrl;

    public VerificationServiceClient(RestTemplateBuilder restTemplateBuilder, MeterRegistry meterRegistry) {
        this.restTemplate = restTemplateBuilder.errorHandler(new DefaultResponseErrorHandler()).build();
        this.verifyNewUserTimer = meterRegistry.timer("verifyNewUser");
    }

    public VerificationResult verifyNewUser(UUID userUuid, int userAge) {
        return verifyNewUserTimer.record(() -> {
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromHttpUrl(fraudVerifyBaseUrl + "/users/verify").queryParam("uuid", userUuid)
                    .queryParam("age", userAge);
                    try {
                        return restTemplate.getForObject(uriComponentsBuilder.toUriString(), VerificationResult.class);
                    } catch (Throwable e) {
                        return userRejected(userUuid, userAge);
                    }
            
        });
    }

    private VerificationResult userRejected(UUID userUuid, int userAge) {
        return VerificationResult.failed(userUuid);
    }
}