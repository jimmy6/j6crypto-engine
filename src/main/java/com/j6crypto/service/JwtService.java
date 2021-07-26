package com.j6crypto.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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

  public String generateToken(String clientId) {
    Claims claims = Jwts.claims().setSubject(clientId);
    long nowMillis = System.currentTimeMillis();
    long expMillis = nowMillis + tokenValidity;
    Date exp = new Date(expMillis);
    return Jwts.builder().setClaims(claims).setIssuedAt(new Date(nowMillis)).setExpiration(exp)
      .signWith(SignatureAlgorithm.HS512, secretBytes).compact();
  }

  public void validateToken(final String token) {
    try {
      Jwts.parser().setSigningKey(secretBytes).parseClaimsJws(token);
//    } catch (SignatureException ex) {
//      throw new JwtTokenMalformedException("Invalid JWT signature");
//    } catch (MalformedJwtException ex) {
//      throw new JwtTokenMalformedException("Invalid JWT token");
//    } catch (ExpiredJwtException ex) {
//      throw new JwtTokenMalformedException("Expired JWT token");
//    } catch (UnsupportedJwtException ex) {
//      throw new JwtTokenMalformedException("Unsupported JWT token");
//    } catch (IllegalArgumentException ex) {
//      throw new JwtTokenMissingException("JWT claims string is empty.");
//    }
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}
