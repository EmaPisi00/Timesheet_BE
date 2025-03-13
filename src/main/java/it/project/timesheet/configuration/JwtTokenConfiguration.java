package it.project.timesheet.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import it.project.timesheet.utils.DateUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenConfiguration {

    private final SecretKey signInKey; // Chiave memorizzata una sola volta

    public JwtTokenConfiguration() {
        this.signInKey = generateSignInKey();
    }

    private SecretKey generateSignInKey() {
        String secretkey;
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGen.generateKey();
            secretkey = Base64.getEncoder().encodeToString(sk.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        byte[] keyBytes = Decoders.BASE64.decode(secretkey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", userDetails.getAuthorities());
        claims.put("email", userDetails.getUsername());
        claims.put("password", userDetails.getPassword());

        // Converto la data di emissione (iat) e scadenza (exp) in formato leggibile
        String formattedIssuedAt = DateUtils.convertDateToString(new Date(System.currentTimeMillis()));
        String formattedExpiration = DateUtils.convertDateToString(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        claims.put("issuedAt", formattedIssuedAt);
        claims.put("expiration", formattedExpiration);

        return createToken(claims, userDetails.getUsername());
    }

    public String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .signWith(getSignInKey())
                .compact();
    }

    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    private SecretKey getSignInKey() {
        return this.signInKey; // Usa sempre la stessa chiave
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())  // Metodo per verificare la firma
                .build()
                .parseSignedClaims(token)    // Parsing dei claims firmati
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
