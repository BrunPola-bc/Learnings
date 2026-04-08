package com.brunpola.cv_management.security.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Utility service for creating, parsing, and validating JSON Web Tokens (JWTs).
 *
 * <p>Provides methods to extract claims, generate tokens with optional extra claims, and validate
 * tokens against a {@link UserDetails} instance. Uses a static secret key for signing and
 * verification.
 */
@Service
public class JwtService {

  /** Constant representing 24h in milliseconds, used as token expiration time */
  private static final int ONE_DAY = 1000 * 60 * 60 * 24;

  /** Secret key used for signing JWTs (base64 encoded). For real applications, store securely. */
  private static final String SECRET_KEY =
      "DA2FC136AC98A6F1BBC601C5C46627DB19727F68D404C7636AB5012B73F8D95A";

  // ========================== Claim Extraction ==========================

  /**
   * Extracts all claims from a JWT.
   *
   * @param jwtToken the JWT string
   * @return the {@link Claims} contained in the token
   */
  public Claims extractAllClaims(String jwtToken) {
    try {
      return Jwts.parser() // JwtParserBuilder
          .verifyWith(getSigningKey()) // still an instance of JwtParserBuilder (just signed now)
          .build() // JwtParser
          .parseSignedClaims(jwtToken) // Jws<Claims>
          .getPayload(); // Claims
    } catch (ExpiredJwtException e) {
      return e.getClaims();
    }
  }

  /**
   * Gets a specific claim from the token.
   *
   * @param <T> the type of the claim to extract
   * @param jwtToken the JWT string
   * @param claimsResolverFunction the function to extract the claim
   * @return the extracted claim
   */
  public <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolverFunction) {
    try {
      final Claims claims = extractAllClaims(jwtToken);
      return claimsResolverFunction.apply(claims);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Extracts the username (subject) from a JWT.
   *
   * @param jwtToken the JWT string
   * @return the username contained in the token
   */
  public String extractUsername(String jwtToken) {
    return extractClaim(jwtToken, Claims::getSubject);
  }

  /**
   * Extracts the expiration date from a JWT.
   *
   * @param jwtToken the JWT string
   * @return the expiration {@link Date} of the token
   */
  public Date extractExpiration(String jwtToken) {
    return extractClaim(jwtToken, Claims::getExpiration);
  }

  // ========================== Token Generation ==========================

  /**
   * Generates a JWT for a given user, including optional extra claims.
   *
   * @param extraClaims additional claims to include in the token
   * @param userDetails the user for whom the token is generated
   * @return the signed JWT string
   */
  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    JwtBuilder builder =
        Jwts.builder()
            .claims(extraClaims)
            .subject(userDetails.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + ONE_DAY))
            .signWith(getSigningKey(), Jwts.SIG.HS256);

    return builder.compact();
  }

  /**
   * Generates a JWT for a given user without extra claims.
   *
   * @param userDetails the user for whom the token is generated
   * @return the signed JWT string
   */
  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  // ========================== Token Validation ==========================

  /**
   * Validates a JWT by checking that it belongs to the given user and is not expired.
   *
   * @param jwtToken the JWT string
   * @param userDetails the user to validate against
   * @return {@code true} if the token is valid, {@code false} otherwise
   */
  public boolean isTokenValid(String jwtToken, UserDetails userDetails) {
    final String username = extractUsername(jwtToken);
    boolean isUsernameValid = username.equals(userDetails.getUsername());

    final Date expirationTime = extractExpiration(jwtToken);
    boolean isTokenNonExpired = expirationTime.after(new Date());

    return isUsernameValid && isTokenNonExpired;
  }

  // ========================== Signing Key ==========================

  /**
   * Returns the secret signing key used for JWT generation and verification.
   *
   * @return a {@link SecretKey} instance
   */
  public SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
