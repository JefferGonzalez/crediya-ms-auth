package co.com.pragma.crediya;

import co.com.pragma.crediya.model.logs.gateways.LoggerPort;
import co.com.pragma.crediya.model.user.Role;
import co.com.pragma.crediya.model.user.User;
import co.com.pragma.crediya.model.user.exceptions.RoleNotFoundException;
import co.com.pragma.crediya.r2dbc.UserReactiveRepository;
import co.com.pragma.crediya.r2dbc.UserRepositoryAdapter;
import co.com.pragma.crediya.usecase.user.UserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
class UserUseCaseIntegrationTest {

    @Autowired
    private UserUseCase userUseCase;

    @Autowired
    private UserReactiveRepository userReactiveRepository;

    @Autowired
    private LoggerPort logger;

    @MockitoSpyBean
    private UserRepositoryAdapter userRepositoryAdapter;

    @BeforeEach
    void setup() {
        userReactiveRepository.deleteAll().block();
    }

    @Test
    void shouldRollbackTransactionWhenUserRepositoryThrowsException() {
        Role role = new Role(null, null, null);
        User user = new User(null, "John", "Doe", null, "johndoe@example.com", null, null, new BigDecimal("2000000"), role);

        doReturn(Mono.error(new RoleNotFoundException()))
                .when(userRepositoryAdapter).save(any(User.class));

        StepVerifier.create(userUseCase.save(user))
                .expectError(RoleNotFoundException.class)
                .verify();

        StepVerifier.create(userReactiveRepository.count())
                .expectNext(0L).verifyComplete();
    }

}
