package co.com.pragma.crediya.api;

import co.com.pragma.crediya.api.dto.EmailsRequest;
import co.com.pragma.crediya.api.dto.LoginRequest;
import co.com.pragma.crediya.api.dto.SaveUserRequest;
import co.com.pragma.crediya.api.dto.TokenResponse;
import co.com.pragma.crediya.api.exceptions.EmptyRequestBodyException;
import co.com.pragma.crediya.api.mapper.UserRestMapper;
import co.com.pragma.crediya.api.validator.ReactiveValidator;
import co.com.pragma.crediya.model.user.constants.UserFieldNames;
import co.com.pragma.crediya.usecase.auth.AuthUseCase;
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

    private final AuthUseCase authUseCase;

    private final UserRestMapper userMapper;

    private final ReactiveValidator reactiveValidator;

    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(LoginRequest.class)
                .switchIfEmpty(Mono.error(new EmptyRequestBodyException()))
                .flatMap(reactiveValidator::validate)
                .flatMap(loginRequest -> authUseCase.authenticate(loginRequest.getEmail(), loginRequest.getPassword()))
                .flatMap(token ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(new TokenResponse(token))
                );
    }

    public Mono<ServerResponse> createUser(ServerRequest request) {
        return request.bodyToMono(SaveUserRequest.class)
                .switchIfEmpty(Mono.error(new EmptyRequestBodyException()))
                .flatMap(reactiveValidator::validate)
                .map(userMapper::toDomain)
                .flatMap(userUseCase::save)
                .map(userMapper::toResponse)
                .flatMap(storedUser ->
                        ServerResponse.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(storedUser)
                );
    }

    public Mono<ServerResponse> getUserByIdentificationNumber(ServerRequest request) {
        String identificationNumber = request.pathVariable(UserFieldNames.IDENTIFICATION_NUMBER);

        return userUseCase.findByIdentificationNumber(identificationNumber)
                .map(userMapper::toResponse)
                .flatMap(user ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(user));
    }

    public Mono<ServerResponse> search(ServerRequest request) {
        return request
                .bodyToMono(EmailsRequest.class)
                .switchIfEmpty(Mono.error(new EmptyRequestBodyException()))
                .flatMap(reactiveValidator::validate)
                .flatMapMany(dto -> userUseCase.findAllByEmails(dto.getEmails()))
                .map(userMapper::toResponse)
                .collectList()
                .flatMap(users ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(users)
                );
    }

}
