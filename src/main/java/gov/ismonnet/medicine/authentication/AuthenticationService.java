package gov.ismonnet.medicine.authentication;

import javax.ws.rs.core.UriInfo;
import java.util.Map;

public interface AuthenticationService {

    Map<String, Object> authenticate(String token) throws AuthenticationException;

    String generateToken(int id, String username, UriInfo uriInfo);
}
