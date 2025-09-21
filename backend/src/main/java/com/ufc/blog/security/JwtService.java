package com.ufc.blog.security;

import com.ufc.blog.entity.User;
import com.ufc.blog.exception.ResourceNotFoundException;
import com.ufc.blog.repository.RefreshTokenRepository;
import com.ufc.blog.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtService.class);
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    @Value("${app.jwt.secret}")
    private String jwtSecret;
    @Value("${app.jwt.issuer}")
    private String issuer;
    @Value("${app.jwt.expiration-ms}")
    private int jwtExpirationInMs;
    @Value("${app.jwt.refresh-expiration-ms}")
    private int jwtRefreshExpirationInMs;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Transactional
    public Map<String, Object> generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        Date refreshExpiryDate = new Date(now.getTime() + jwtRefreshExpirationInMs);

        SecretKey key = getSigningKey();

        String accessToken = Jwts.builder()
                .subject(Long.toString(userPrincipal.getId()))
                .issuer(issuer)
                .issuedAt(new Date())
                .expiration(expiryDate)
                .signWith(key)
                .compact();

        String refreshToken;
        Optional<RefreshToken> existingRefreshTokenOpt =
                refreshTokenRepository.findByUser(userPrincipal.getUser());

        if (existingRefreshTokenOpt.isPresent() && existingRefreshTokenOpt.get().getExpiryDate().isAfter(Instant.now().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())) {
            refreshToken = existingRefreshTokenOpt.get().getToken();
        } else {
            RefreshToken tokenEntity = existingRefreshTokenOpt.orElseGet(RefreshToken::new);
            tokenEntity.setUser(userPrincipal.getUser());
            tokenEntity.setExpiryDate(refreshExpiryDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
            tokenEntity.setToken(Jwts.builder()
                    .subject(Long.toString(userPrincipal.getId()))
                    .issuer(issuer)
                    .issuedAt(new Date())
                    .expiration(refreshExpiryDate)
                    .signWith(key)
                    .compact());

            refreshToken = refreshTokenRepository.save(tokenEntity).getToken();
        }

        User user = userRepository
                .findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userPrincipal.getId()));

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "user", user
        );
    }

    public Long getUserIdFromToken(String token) {
        SecretKey key = getSigningKey();

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            SecretKey key = getSigningKey();
            Jwts.parser()
                    .verifyWith(key).build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (SignatureException ex) {
            LOGGER.error("Assinatura do token JWT inválida");
        } catch (MalformedJwtException ex) {
            LOGGER.error("Token JWT malformado");
        } catch (ExpiredJwtException ex) {
            LOGGER.error("Token JWT expirado");
        } catch (UnsupportedJwtException ex) {
            LOGGER.error("Token JWT não suportado");
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Argumento do token JWT está vazio");
        }
        return false;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())) {
            refreshTokenRepository.delete(token);
        }
        return token;
    }

    public String generateTokenFromUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "username", username));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        SecretKey key = getSigningKey();

        return Jwts.builder()
                .subject(Long.toString(user.getId()))
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }
}
