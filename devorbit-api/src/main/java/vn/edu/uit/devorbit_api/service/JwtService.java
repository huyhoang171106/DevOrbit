package vn.edu.uit.devorbit_api.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.edu.uit.devorbit_api.config.JwtProperties;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    private static final String DEFAULT_SECRET_SENTINEL = "default-jwt-secret-key-long-enough-256bits";

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtService(JwtProperties jwtProperties,
                      @Value("${spring.profiles.active:}") String activeProfiles) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.secret().getBytes(StandardCharsets.UTF_8)
        );
        if (DEFAULT_SECRET_SENTINEL.equals(jwtProperties.secret())) {
            if (activeProfiles.contains("prod") || activeProfiles.contains("staging")) {
                throw new IllegalStateException(
                    "JWT_SECRET must be configured for production! Do not use the default value.");
            }
            log.warn("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            log.warn("! JWT secret is set to the default value!                     ");
            log.warn("! Set JWT_SECRET environment variable in production!           ");
            log.warn("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
    }

    private static final int ACCESS_TOKEN_EXPIRATION_MINUTES = 15;

    public String generateAccessToken(String username, String tokenType) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(username)
                .claim("type", tokenType)
                .claim("tokenKind", "ACCESS")
                .issuedAt(new Date(now))
                .expiration(new Date(now + ACCESS_TOKEN_EXPIRATION_MINUTES * 60 * 1000L))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(String username, String tokenType) {
        long now = System.currentTimeMillis();
        long refreshMillis = jwtProperties.refreshExpirationDays() * 24L * 60 * 60 * 1000;
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(username)
                .claim("type", tokenType)
                .claim("tokenKind", "REFRESH")
                .issuedAt(new Date(now))
                .expiration(new Date(now + refreshMillis))
                .signWith(secretKey)
                .compact();
    }

    // ----- backward compat delegators -----

    public String generateToken(String username) {
        return generateAccessToken(username, "ADMIN");
    }

    public String generateToken(String username, String tokenType) {
        return generateAccessToken(username, tokenType);
    }

    // ----- extraction -----

    public String extractTokenKind(String token) {
        Claims claims = parseToken(token);
        String kind = claims.get("tokenKind", String.class);
        return kind != null ? kind : "ACCESS";
    }

    public String extractJti(String token) {
        return parseToken(token).getId();
    }

    public String extractUsername(String token) {
        return parseToken(token).getSubject();
    }

    public String extractTokenType(String token) {
        Claims claims = parseToken(token);
        String type = claims.get("type", String.class);
        return type != null ? type : "USER";
    }

    public Instant extractExpiration(String token) {
        return parseToken(token).getExpiration().toInstant();
    }

    public boolean isTokenValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
