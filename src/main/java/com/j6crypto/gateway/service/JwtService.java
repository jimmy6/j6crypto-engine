package com.j6crypto.gateway.service;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.util.Date;

/**
 * @author <a href="mailto:laiseong@gmail.com">Jimmy Au</a>
 * @see <a href="https://roytuts.com/spring-cloud-gateway-security-with-jwt-json-web-token/">Tutorial</>
 */
@Component
public class JwtService {
  private static String key = "i_am_sobi_am_i";
  private static byte[] secretBytes = DatatypeConverter.parseBase64Binary(key);

  @Value("${jwt.token.validity}")
  private long tokenValidity;

  public Claims getClaims(final String token) {
    try {
      Claims body = Jwts.parser().setSigningKey(secretBytes).parseClaimsJws(token).getBody();
      return body;
    } catch (Exception e) {
      System.out.println(e.getMessage() + " => " + e);
    }
    return null;
  }

  public String generateToken(String id) {
    Claims claims = Jwts.claims().setSubject(id);
    long nowMillis = System.currentTimeMillis();
    long expMillis = nowMillis + tokenValidity;
    Date exp = new Date(expMillis);
    return Jwts.builder().setClaims(claims).setIssuedAt(new Date(nowMillis)).setExpiration(exp)
      .signWith(SignatureAlgorithm.HS512, secretBytes).compact();
  }

  public void validateToken(final String token) {
    try {
      Jwts.parser().setSigningKey(secretBytes).parseClaimsJws(token);
    } catch (SignatureException ex) {
      throw new RuntimeException("Invalid JWT signature");
    } catch (MalformedJwtException ex) {
      throw new RuntimeException("Invalid JWT token");
    } catch (ExpiredJwtException ex) {
      throw new RuntimeException("Expired JWT token");
    } catch (UnsupportedJwtException ex) {
      throw new RuntimeException("Unsupported JWT token");
    } catch (IllegalArgumentException ex) {
      throw new RuntimeException("JWT claims string is empty.");
    }
  }
}
