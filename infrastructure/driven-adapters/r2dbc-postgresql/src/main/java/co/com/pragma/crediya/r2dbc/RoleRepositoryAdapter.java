package co.com.pragma.crediya.r2dbc;

import co.com.pragma.crediya.model.user.Role;
import co.com.pragma.crediya.model.user.gateways.RoleRepository;
import co.com.pragma.crediya.r2dbc.entity.RoleEntity;
import co.com.pragma.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class RoleRepositoryAdapter
        extends ReactiveAdapterOperations<Role, RoleEntity, UUID, RoleReactiveRepository> implements RoleRepository {

    public RoleRepositoryAdapter(RoleReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> new Role(d.getId(), d.getName(), d.getDescription()));
    }

    @Override
    public Mono<Role> findByName(String role) {
        return repository.findByName(role).map(this::toEntity);
    }
}
