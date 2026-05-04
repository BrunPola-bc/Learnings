package com.brunpola.api_gateway.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtUtil {

  private static final int ONE_DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
  private static final String SECRET_KEY =
      "DA2FC136AC98A6F1BBC601C5C46627DB19727F68D404C7636AB5012B73F8D95A";

  // ========================== Claim Extraction ==========================

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

  public <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolverFunction) {
    try {
      final Claims claims = extractAllClaims(jwtToken);
      return claimsResolverFunction.apply(claims);
    } catch (Exception e) {
      return null;
    }
  }

  public String extractUsername(String jwtToken) {
    return extractClaim(jwtToken, Claims::getSubject);
  }

  public Date extractExpiration(String jwtToken) {
    return extractClaim(jwtToken, Claims::getExpiration);
  }

  public List<String> extractRoles(String jwtToken) {
    return ((List<?>) extractClaim(jwtToken, claims -> claims.get("roles")))
        .stream().map(String::valueOf).toList();
  }

  // ========================== Token Generation ==========================

  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    JwtBuilder builder =
        Jwts.builder()
            .claims(extraClaims)
            .subject(userDetails.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + ONE_DAY_IN_MILLIS))
            .signWith(getSigningKey(), Jwts.SIG.HS256);

    return builder.compact();
  }

  public String generateToken(UserDetails userDetails) {

    HashMap<String, Object> extraClaims = new HashMap<>();

    extraClaims.put(
        "roles",
        userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());

    return generateToken(extraClaims, userDetails);
  }

  // ========================== Token Validation ==========================

  public boolean isTokenValid(String jwtToken) {
    try {
      extractAllClaims(jwtToken); // verifies signature
      return extractExpiration(jwtToken).after(new Date());
    } catch (Exception e) {
      return false;
    }
  }

  // ========================== Signing Key ==========================

  public SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
