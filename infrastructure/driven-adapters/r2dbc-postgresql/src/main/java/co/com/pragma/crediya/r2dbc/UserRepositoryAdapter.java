package co.com.pragma.crediya.r2dbc;

import co.com.pragma.crediya.model.user.User;
import co.com.pragma.crediya.model.user.exceptions.RoleNotFoundException;
import co.com.pragma.crediya.model.user.gateways.UserRepository;
import co.com.pragma.crediya.r2dbc.entity.UserEntity;
import co.com.pragma.crediya.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.crediya.r2dbc.mapper.UserMapper;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class UserRepositoryAdapter
        extends ReactiveAdapterOperations<User, UserEntity, UUID, UserReactiveRepository> implements UserRepository {

    private final RoleReactiveRepository roleReactiveRepository;

    public UserRepositoryAdapter(UserReactiveRepository userReactiveRepository, ObjectMapper mapper, RoleReactiveRepository roleReactiveRepository) {
        super(userReactiveRepository, mapper, d -> mapper.map(d, User.class));

        this.roleReactiveRepository = roleReactiveRepository;
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Mono<User> save(User user) {
        return roleReactiveRepository.findByName(user.role().name())
                .switchIfEmpty(Mono.error(new RoleNotFoundException()))
                .flatMap(roleEntity -> repository.save(UserMapper.toEntity(user))
                        .map(userEntity -> UserMapper.toDomain(userEntity, roleEntity)));
    }

}
