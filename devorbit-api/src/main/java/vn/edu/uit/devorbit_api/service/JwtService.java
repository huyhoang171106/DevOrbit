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

    public String generateToken(String username) {
        return generateToken(username, "ADMIN");
    }

    public String generateToken(String username, String tokenType) {
        return generateToken(username, tokenType, null);
    }

    public String generateToken(String username, String tokenType, Integer tokenVersion) {
        long now = System.currentTimeMillis();
        var builder = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(username)
                .claim("type", tokenType)
                .issuedAt(new Date(now))
                .expiration(new Date(now + jwtProperties.expirationMinutes() * 60 * 1000));
        if (tokenVersion != null) {
            builder.claim("tokenVersion", tokenVersion);
        }
        return builder.signWith(secretKey).compact();
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

    public int extractTokenVersion(String token) {
        Claims claims = parseToken(token);
        Object version = claims.get("tokenVersion");
        if (version instanceof Number number) {
            return number.intValue();
        }
        return 0;
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
