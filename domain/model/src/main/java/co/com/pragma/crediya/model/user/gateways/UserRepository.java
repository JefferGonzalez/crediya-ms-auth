package co.com.pragma.crediya.model.user.gateways;

import co.com.pragma.crediya.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserRepository {

    Mono<Boolean> existsByEmail(String email);

    Mono<Boolean> existsByIdentificationNumber(String identificationNumber);

    Mono<User> findByIdentificationNumber(String identificationNumber);

    Mono<User> findByEmail(String email);

    Flux<User> findAllByEmailIn(List<String> emails);

    Mono<User> save(User user);

}
