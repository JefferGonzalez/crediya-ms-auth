package co.com.pragma.crediya.r2dbc;

import co.com.pragma.crediya.model.user.Role;
import co.com.pragma.crediya.model.user.gateways.RoleRepository;
import co.com.pragma.crediya.r2dbc.mapper.RoleDatabaseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepository {

    private final RoleReactiveRepository repository;

    private final RoleDatabaseMapper mapper;

    @Override
    public Mono<Role> findByName(String role) {
        return repository.findByName(role)
                .map(mapper::toDomain);
    }
}
