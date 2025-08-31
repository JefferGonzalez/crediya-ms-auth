package co.com.pragma.crediya.api;

import co.com.pragma.crediya.api.dto.SaveUserRequest;
import co.com.pragma.crediya.api.dto.UserResponse;
import co.com.pragma.crediya.api.mapper.UserRestMapper;
import co.com.pragma.crediya.api.validator.ReactiveValidator;
import co.com.pragma.crediya.model.logs.gateways.LoggerPort;
import co.com.pragma.crediya.model.user.User;
import co.com.pragma.crediya.model.common.constants.DomainConstants;
import co.com.pragma.crediya.usecase.user.UserUseCase;
import jakarta.validation.Validator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, UserHandler.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ReactiveValidator reactiveValidator;

    @MockitoBean
    private UserRestMapper userRestMapper;

    @MockitoBean
    private Validator validator;

    @MockitoBean
    private LoggerPort logger;

    @MockitoBean
    private UserUseCase userUseCase;

    private SaveUserRequest request;

    private User user;

    private UserResponse userResponse;

    private final UUID userId = UUID.fromString("7171da0f-12e1-4e04-a2d6-a7dc43d6d411");

    @BeforeEach
    void setup() {
        request = SaveUserRequest.builder()
                .names("John")
                .lastName("Doe")
                .identificationNumber("123456789")
                .email("johndoe@example.com")
                .baseSalary("15000000")
                .build();

        user = new User(userId, "John", "Doe", null, "123456789", "johndoe@example.com", null, null, new BigDecimal("15000000"), null);

        userResponse = UserResponse.builder()
                .id(userId)
                .names("John")
                .lastName("Doe")
                .identificationNumber("123456789")
                .email("johndoe@example.com")
                .baseSalary(new BigDecimal("15000000"))
                .rol(DomainConstants.DEFAULT_ROLE)
                .build();

        when(reactiveValidator.validate(any())).thenAnswer(invocation ->
                Mono.just(invocation.getArgument(0))
        );

        when(userRestMapper.toDomain(any(SaveUserRequest.class))).thenReturn(user);
    }

    @Test
    void saveUser_shouldReturnCreated_whenValidRequest() {
        when(userUseCase.save(any(User.class))).thenReturn(Mono.just(user));

        when(userRestMapper.toResponse(any(User.class))).thenReturn(userResponse);

        webTestClient.post()
                .uri("/api/v1/users")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponse.class)
                .value(response -> {
                    Assertions.assertThat(response).isInstanceOf(UserResponse.class);
                    Assertions.assertThat(response.getId()).isNotNull();
                    Assertions.assertThat(response.getId()).isEqualTo(userId);
                    Assertions.assertThat(response.getRol()).isEqualTo(DomainConstants.DEFAULT_ROLE);
                });
    }
}
