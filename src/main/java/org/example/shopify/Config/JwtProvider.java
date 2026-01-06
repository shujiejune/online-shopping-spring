package org.example.shopify.Config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String SIGNING_KEY;

    private final long EXPIRATION_HOURS = 24;

    public String createToken(String username) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusHours(EXPIRATION_HOURS);

        // Convert to Date for JJWT compatibility
        Date issuedAt = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        Date expiresAt = Date.from(expiry.atZone(ZoneId.systemDefault()).toInstant());

        byte[] keyBytes = SIGNING_KEY.getBytes(StandardCharsets.UTF_8);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(issuedAt)
                .setExpiration(expiresAt)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
