package gov.ismonnet.medicine.authentication;

import com.google.common.annotations.VisibleForTesting;
import gov.ismonnet.medicine.persistence.KeyStoreService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.ZonedDateTime;

public class JwtAuthenticationService implements AuthenticationService {

    private final Key key;
    private final JwtParser jwtParser;

    @Inject JwtAuthenticationService(KeyStoreService keyStoreService)
            throws UnrecoverableKeyException, CertificateException, KeyStoreException {

        Key key = keyStoreService.getKey("jwt_key");
        if(key == null) {
            key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
            keyStoreService.setKeyEntry("jwt_key", key, null);
            keyStoreService.store();
        }

        this.key = key;
        this.jwtParser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();
    }

    @VisibleForTesting JwtAuthenticationService(Key key) {
        this.key = key;
        this.jwtParser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();
    }

    @Override
    public String authenticate(String token) {
        try {
            return jwtParser.parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException ex) {
            throw new AuthenticationException(ex);
        }
    }

    @Override
    public String generateToken(String username, UriInfo uriInfo) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuer(uriInfo.getAbsolutePath().toString())
                .setIssuedAt(new java.util.Date())
                .setExpiration(java.util.Date.from(ZonedDateTime.now().plusMinutes(15L).toInstant()))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
}
