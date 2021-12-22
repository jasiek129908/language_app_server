package com.example.server_foregin_languages.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;

@Service
public class JwtProvider {

    @Value("${jwt.privateKey}")
    private String privateKeyToToken;
    private final Long expirationTimeInSeconds = 60l*60;

    public String generateToken(Authentication authenticate) {
        UserDetails userDetails = (UserDetails) authenticate.getPrincipal();
        String token = JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(Instant.now().plusSeconds(expirationTimeInSeconds)))
                .sign(Algorithm.HMAC256(privateKeyToToken));
        return token;
    }

    public Long getExpirationTimeInSeconds() {
        return expirationTimeInSeconds;
    }

    public boolean validateToken(String jwt) {
        Algorithm algorithm = Algorithm.HMAC256(privateKeyToToken);
        JWTVerifier verifier = JWT.require(algorithm).build();
        verifier.verify(jwt);
        return true;
    }

    public String getEmailFromJwt(String jwt) {
        Algorithm algorithm = Algorithm.HMAC256(privateKeyToToken);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(jwt);
        return decodedJWT.getSubject();
    }

    public String generateTokenWithUserMail(String email) {
        String token = JWT.create()
                .withSubject(email)
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(Instant.now().plusSeconds(expirationTimeInSeconds)))
                .sign(Algorithm.HMAC256(privateKeyToToken));
        return token;
    }
}
