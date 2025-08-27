package co.com.pragma.crediya.api;

import co.com.pragma.crediya.api.dto.SaveUserRequest;
import co.com.pragma.crediya.api.dto.UserResponse;
import co.com.pragma.crediya.api.validator.ReactiveValidator;
import co.com.pragma.crediya.model.logs.gateways.LoggerPort;
import co.com.pragma.crediya.model.user.Role;
import co.com.pragma.crediya.model.user.User;
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
import java.time.LocalDate;
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
    private Validator validator;

    @MockitoBean
    private LoggerPort logger;

    @MockitoBean
    private UserUseCase userUseCase;

    private SaveUserRequest request;

    @BeforeEach
    void setup() {
        when(reactiveValidator.validate(any())).thenAnswer(invocation ->
                Mono.just(invocation.getArgument(0))
        );

        request = SaveUserRequest.builder()
                .names("John")
                .lastName("Doe")
                .birthDate("1980-01-01")
                .email("johndoe@example.com")
                .address("Unknown")
                .phoneNumber("123456789")
                .baseSalary("15000000")
                .build();
    }

    @Test
    void saveUser_shouldReturnCreated_whenValidRequest() {
        Role role = new Role(null, "CUSTOMER", null);
        User user = new User(UUID.randomUUID(), "John", "Doe", LocalDate.of(1980, 1, 1), "johndoe@example.com", "Unknown", "123456789", new BigDecimal("15000000"), role);

        when(userUseCase.save(any(User.class))).thenReturn(Mono.just(user));

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
                    Assertions.assertThat(response.getRol()).isEqualTo("CUSTOMER");
                });
    }
}
