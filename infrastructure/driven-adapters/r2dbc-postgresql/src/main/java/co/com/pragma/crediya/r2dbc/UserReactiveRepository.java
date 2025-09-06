package co.com.pragma.crediya.r2dbc;

import co.com.pragma.crediya.r2dbc.entity.UserEntity;
import co.com.pragma.crediya.r2dbc.projection.UserProjection;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface UserReactiveRepository
        extends ReactiveCrudRepository<UserEntity, UUID>, ReactiveQueryByExampleExecutor<UserEntity> {

    Mono<Boolean> existsByEmail(String name);

    Mono<Boolean> existsByIdentificationNumber(String identificationNumber);

    Mono<UserEntity> findByIdentificationNumber(String identificationNumber);

    Mono<UserEntity> findByEmail(String email);

    Flux<UserProjection> findAllByEmailIn(List<String> emails);

}
