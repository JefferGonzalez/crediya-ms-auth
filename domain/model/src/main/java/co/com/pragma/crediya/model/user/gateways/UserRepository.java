package co.com.pragma.crediya.model.user.gateways;

import co.com.pragma.crediya.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<Boolean> existsByEmail(String email);

    Mono<User> save(User user);

}
