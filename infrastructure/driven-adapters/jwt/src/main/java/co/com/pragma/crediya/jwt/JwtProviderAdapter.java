package co.com.pragma.crediya.jwt;

import co.com.pragma.crediya.jwt.config.JwtProperties;
import co.com.pragma.crediya.model.jwt.Jwt;
import co.com.pragma.crediya.model.jwt.gateways.JwtProviderPort;
import co.com.pragma.crediya.model.logs.gateways.LoggerPort;
import co.com.pragma.crediya.model.user.User;
import co.com.pragma.crediya.model.user.constants.UserFieldNames;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtProviderAdapter implements JwtProviderPort {

    private final JwtProperties properties;

    private final LoggerPort logger;

    @Override
    public String generateToken(User user) {
        return buildToken(user, properties.getExpiration());
    }

    @Override
    public Jwt parseToken(String token) {
        Claims claims = extractClaims(token);

        String subject = extractSubject(claims);
        List<String> roles = extractRole(claims);
        String identificationNumber = extractIdentificationNumber(claims);

        return new Jwt(subject, roles, identificationNumber);
    }

    @Override
    public boolean validate(String token) {
        if (token == null || token.trim().isEmpty()) {
            logger.warn("JWT validation failed: Token is null or empty");
            return false;
        }

        try {
            Claims claims = extractClaims(token);
            String subject = extractSubject(claims);

            if (subject == null || subject.trim().isEmpty()) {
                logger.warn("JWT validation failed: Subject is null or empty");
                return false;
            }

            logger.info("JWT validation successful for subject: {}", subject);

            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("JWT validation failed: Token expired for subject: {}", e.getClaims().getSubject());
            return false;
        } catch (UnsupportedJwtException e) {
            logger.warn("JWT validation failed: Unsupported token format");
            return false;
        } catch (MalformedJwtException e) {
            logger.warn("JWT validation failed: Malformed token structure");
            return false;
        } catch (IllegalArgumentException e) {
            logger.warn("JWT validation failed: Invalid arguments provided");
            return false;
        } catch (Exception e) {
            logger.error("JWT validation failed: Unexpected error occurred", e);
            return false;
        }
    }

    private String buildToken(User user, long expiration) {
        Map<String, Object> claims = buildClaims(user);
        Date issuedAt = getCurrentDate();
        Date expirationDate = calculateExpirationDate(expiration);

        return Jwts.builder()
                .id(user.id().toString())
                .subject(user.email())
                .claims(claims)
                .issuedAt(issuedAt)
                .expiration(expirationDate)
                .signWith(getSecretKey())
                .compact();
    }

    private SecretKey getSecretKey() {
        String secretKey = properties.getSecretKey();
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Map<String, Object> buildClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(UserFieldNames.ROLES, List.of(user.role().name()));
        claims.put(UserFieldNames.IDENTIFICATION_NUMBER, user.identificationNumber());
        return claims;
    }

    private Date calculateExpirationDate(long expiration) {
        return new Date(System.currentTimeMillis() + expiration);
    }

    private Date getCurrentDate() {
        return new Date();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @SuppressWarnings("unchecked")
    private List<String> extractRole(Claims claims) {
        return (List<String>) claims.get(UserFieldNames.ROLES);
    }

    private String extractIdentificationNumber(Claims claims) {
        return claims.get(UserFieldNames.IDENTIFICATION_NUMBER).toString();
    }

    private String extractSubject(Claims claims) {
        return claims.getSubject();
    }

}
