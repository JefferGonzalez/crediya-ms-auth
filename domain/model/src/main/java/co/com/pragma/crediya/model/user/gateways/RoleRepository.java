package co.com.pragma.crediya.model.user.gateways;

import co.com.pragma.crediya.model.user.Role;
import reactor.core.publisher.Mono;

public interface RoleRepository {

    Mono<Role> findByName(String role);

}
