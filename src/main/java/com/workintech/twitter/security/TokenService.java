package com.workintech.twitter.security;

import com.workintech.twitter.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final long expirationMinutes;

    public TokenService(JwtEncoder jwtEncoder, @Value("${jwt.expiration-minutes}") long expirationMinutes) {
        this.jwtEncoder = jwtEncoder;
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(User user) {
        Instant now = Instant.now();

        // Token'in icindeki bilgiler (claims). Dikkat: JWT SIFRELENMEZ, sadece IMZALANIR;
        // herkes icini okuyabilir (jwt.io). O yuzden buraya asla sifre vb. koymuyoruz.
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("twitter-api")
                .issuedAt(now)
                .expiresAt(now.plus(expirationMinutes, ChronoUnit.MINUTES))
                .subject(String.valueOf(user.getId()))   // "bu token kimin?" -> kullanici id'si
                .claim("username", user.getUsername())
                .build();

        // Header'da algoritmayi acikca HS256 seciyoruz (varsayilan RS256'dir).
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public long getExpirationSeconds() {
        return expirationMinutes * 60;
    }
}
