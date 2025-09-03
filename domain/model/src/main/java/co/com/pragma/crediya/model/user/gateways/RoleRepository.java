package co.com.pragma.crediya.model.user.gateways;

import co.com.pragma.crediya.model.user.Role;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RoleRepository {

    Mono<Role> findById(UUID id);

    Mono<Role> findByName(String role);

}
