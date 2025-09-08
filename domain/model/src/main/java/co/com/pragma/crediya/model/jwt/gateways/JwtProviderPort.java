package co.com.pragma.crediya.model.jwt.gateways;

import co.com.pragma.crediya.model.jwt.Jwt;
import co.com.pragma.crediya.model.user.User;

public interface JwtProviderPort {

    String generateToken(User user);

    Jwt parseToken(String token);

}
