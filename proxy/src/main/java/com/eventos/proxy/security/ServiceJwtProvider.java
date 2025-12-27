package com.eventos.proxy.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
public class ServiceJwtProvider {

    private final Algorithm algorithm;
    private final String issuer;
    private final String expectedIssuer;
    private final long ttlSeconds;
    private final JWTVerifier verifier;

    public ServiceJwtProvider(
            @Value("${service.jwt.secret}") String secret,
            @Value("${service.jwt.issuer}") String issuer,
            @Value("${service.jwt.expected-issuer}") String expectedIssuer,
            @Value("${service.jwt.ttl-seconds}") long ttlSeconds) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.issuer = issuer;
        this.expectedIssuer = expectedIssuer;
        this.ttlSeconds = ttlSeconds;
        this.verifier = JWT.require(algorithm)
                .withIssuer(expectedIssuer)
                .build();
    }

    public String createToken() {
        Instant now = Instant.now();
        return JWT.create()
                .withIssuer(issuer)
                .withSubject("service")
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusSeconds(ttlSeconds)))
                .sign(algorithm);
    }

    public boolean isValid(String token) {
        try {
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }
}
