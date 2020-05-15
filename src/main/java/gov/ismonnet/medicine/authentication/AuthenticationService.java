package gov.ismonnet.medicine.authentication;

import javax.ws.rs.core.UriInfo;

public interface AuthenticationService {

    String authenticate(String token) throws AuthenticationException;

    String generateToken(String username, UriInfo uriInfo);
}
