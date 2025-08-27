package co.com.pragma.crediya.api;

import co.com.pragma.crediya.api.dto.SaveUserRequest;
import co.com.pragma.crediya.api.mapper.UserMapper;
import co.com.pragma.crediya.api.validator.ReactiveValidator;
import co.com.pragma.crediya.model.user.exceptions.EmptyRequestBodyException;
import co.com.pragma.crediya.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserHandler {

    private final UserUseCase userUseCase;

    private final ReactiveValidator reactiveValidator;

    public Mono<ServerResponse> listenPOSTSaveUserUseCase(ServerRequest request) {
        return request.bodyToMono(SaveUserRequest.class)
                .switchIfEmpty(Mono.error(new EmptyRequestBodyException()))
                .flatMap(reactiveValidator::validate)
                .map(UserMapper::toDomain)
                .flatMap(userUseCase::save)
                .map(UserMapper::toResponse)
                .flatMap(storedUser ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(storedUser)
                );
    }
}
