package gov.ismonnet.medicine.authentication;

import com.google.common.annotations.VisibleForTesting;
import gov.ismonnet.medicine.persistence.KeyStoreService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
    public Map<String, Object> authenticate(String token) {
        try {
            final Map<String, Object> map = new HashMap<>();

            final Claims claims = jwtParser.parseClaimsJws(token).getBody();
            map.put("id", claims.get("id", Integer.class));
            map.put("email", claims.getSubject());

            return Collections.unmodifiableMap(map);
        } catch (JwtException ex) {
            throw new AuthenticationException(ex);
        }
    }

    @Override
    public String generateToken(int id, String username, UriInfo uriInfo) {
        return Jwts.builder()
                .claim("id", id)
                .setSubject(username)
                .setIssuer(uriInfo.getAbsolutePath().toString())
                .setIssuedAt(new java.util.Date())
                .setExpiration(java.util.Date.from(ZonedDateTime.now().plusHours(1L).toInstant()))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
}
