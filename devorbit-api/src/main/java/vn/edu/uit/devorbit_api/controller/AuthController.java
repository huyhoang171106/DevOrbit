package vn.edu.uit.devorbit_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.auth.RefreshRequest;
import vn.edu.uit.devorbit_api.dto.auth.TokenPairResponse;
import vn.edu.uit.devorbit_api.exception.UnauthorizedException;
import vn.edu.uit.devorbit_api.service.JwtService;
import vn.edu.uit.devorbit_api.service.RevokedTokenStore;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final RevokedTokenStore revokedTokenStore;

    @PostMapping("/refresh")
    public TokenPairResponse refresh(@RequestBody @Valid RefreshRequest request) {
        String oldRefreshToken = request.refreshToken();

        // Must be a valid token
        if (!jwtService.isTokenValid(oldRefreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        // Must be a REFRESH token kind
        String tokenKind = jwtService.extractTokenKind(oldRefreshToken);
        if (!"REFRESH".equals(tokenKind)) {
            throw new UnauthorizedException("Not a refresh token");
        }

        // Must not have been revoked already (replay prevention)
        String jti = jwtService.extractJti(oldRefreshToken);
        if (revokedTokenStore.isRevoked(jti)) {
            throw new UnauthorizedException("Refresh token has already been used");
        }

        // Revoke old refresh token (rotation)
        var expiresAt = jwtService.extractExpiration(oldRefreshToken);
        revokedTokenStore.revoke(jti, expiresAt);

        String username = jwtService.extractUsername(oldRefreshToken);
        String type = jwtService.extractTokenType(oldRefreshToken);
        String newAccess = jwtService.generateAccessToken(username, type);
        String newRefresh = jwtService.generateRefreshToken(username, type);
        return new TokenPairResponse(newAccess, newRefresh, type);
    }
}
