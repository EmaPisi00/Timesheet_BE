package it.project.timesheet.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import it.project.timesheet.utils.DateUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenConfiguration {

    private final SecretKey signInKey;
    private static final String KEY = "f38fc19387ffa32ef32893a751c72f4603e28679fca4f38d46ffeba598e8060c";

    public JwtTokenConfiguration() {
        this.signInKey = loadSignKey();
    }

    private SecretKey loadSignKey() {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(KEY);
        return new SecretKeySpec(keyBytes, "HmacSHA256");
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

    public String refreshToken(String token, UserDetails userDetails) {
        try {
            // Estrarre i claims dal token esistente
            Claims claims = extractAllClaims(token);

            // Verifica che il token sia ancora valido (non scaduto)
            if (isTokenExpired(token)) {
                throw new RuntimeException("Token scaduto, impossibile rinnovarlo");
            }

            // Rigenerare il token con le nuove date
            return Jwts.builder()
                    .claims(claims)
                    .subject(userDetails.getUsername())
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                    .signWith(getSignInKey())
                    .compact();

        } catch (Exception e) {
            throw new RuntimeException("Errore durante il refresh del token: " + e.getMessage());
        }
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
