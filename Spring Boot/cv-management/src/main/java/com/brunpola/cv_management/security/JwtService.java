package com.brunpola.cv_management.security;

import io.jsonwebtoken.Claims;
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

@Service
public class JwtService {

  private static final String SECRET_KEY =
      "DA2FC136AC98A6F1BBC601C5C46627DB19727F68D404C7636AB5012B73F8D95A";

  /***** Implementation of extracting claims from the token *****/

  // Get ALL CLAIMS from the token
  public Claims extractAllClaims(String jwtToken) {
    return Jwts.parser() // JwtParserBuilder
        .verifyWith(getSigningKey()) // still an instance of JwtParserBuilder (just signed now)
        .build() // JwtParser
        .parseSignedClaims(jwtToken) // Jws<Claims>
        .getPayload(); // Claims
  }

  // Get a SPECIFIC CLAIM from the token
  public <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolverFunction) {
    final Claims claims = extractAllClaims(jwtToken);
    return claimsResolverFunction.apply(claims);
  }

  // Get USERNAME (the subject) from the token
  public String extractUsername(String jwtToken) {
    return extractClaim(jwtToken, Claims::getSubject);
  }

  // Get EXPIRATION TIME from the token
  public Date extractExpiration(String jwtToken) {
    return extractClaim(jwtToken, Claims::getExpiration);
  }

  /***** Token Generation Implementation *****/

  // From UserDetails + extra claims
  // default claims are username(subject), issued at, expiration and signature
  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    JwtBuilder builder =
        Jwts.builder()
            .claims(extraClaims)
            .subject(userDetails.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
            .signWith(getSigningKey(), Jwts.SIG.HS256);

    return builder.compact(); // String
  }

  // From UserDetails only (no extra claims)
  // just pass through with an empty map of extra claims
  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  /***** Token Validation Implementation *****/

  // We check if the token belongs to the user
  // and if the token is still non expired
  public boolean isTokenValid(String jwtToken, UserDetails userDetails) {
    final String username = extractUsername(jwtToken);
    boolean isUsernameValid = username.equals(userDetails.getUsername());

    final Date expirationTime = extractExpiration(jwtToken);
    boolean isTokenNonExpired = expirationTime.after(new Date());

    return isUsernameValid && isTokenNonExpired;
  }

  /***** Signing Key Implementation *****/

  // Make a SecretKey from the SECRET_KEY variable
  private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
