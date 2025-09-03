package co.com.pragma.crediya.r2dbc;

import co.com.pragma.crediya.model.user.User;
import co.com.pragma.crediya.model.user.gateways.UserRepository;
import co.com.pragma.crediya.r2dbc.mapper.UserDatabaseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserReactiveRepository userReactiveRepository;

    private final UserDatabaseMapper userMapper;

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return userReactiveRepository.existsByEmail(email);
    }

    @Override
    public Mono<Boolean> existsByIdentificationNumber(String identificationNumber) {
        return userReactiveRepository.existsByIdentificationNumber(identificationNumber);
    }

    @Override
    public Mono<User> findByIdentificationNumber(String identificationNumber) {
        return userReactiveRepository.findByIdentificationNumber(identificationNumber)
                .map(userMapper::toDomain);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return userReactiveRepository.findByEmail(email)
                .map(userMapper::toDomainForAuth);
    }

    @Override
    public Mono<User> save(User user) {
        return userReactiveRepository.save(userMapper.toEntity(user))
                .map(userMapper::toDomain);
    }

}
