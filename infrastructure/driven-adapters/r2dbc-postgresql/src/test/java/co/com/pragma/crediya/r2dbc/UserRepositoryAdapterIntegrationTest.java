package co.com.pragma.crediya.r2dbc;

import co.com.pragma.crediya.model.user.Role;
import co.com.pragma.crediya.model.user.User;
import co.com.pragma.crediya.model.user.exceptions.RoleNotFoundException;
import co.com.pragma.crediya.r2dbc.entity.RoleEntity;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import lombok.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.reactivecommons.utils.ObjectMapper;
import org.reactivecommons.utils.ObjectMapperImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = UserRepositoryAdapterIntegrationTest.TestConfig.class)
class UserRepositoryAdapterIntegrationTest {

    @Configuration
    @EnableR2dbcRepositories(
            basePackages = "co.com.pragma.crediya.r2dbc",
            includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                    UserReactiveRepository.class,
                    RoleReactiveRepository.class
            }))
    static class TestConfig extends AbstractR2dbcConfiguration {

        @Override
        @Bean
        @NonNull
        public ConnectionFactory connectionFactory() {
            return ConnectionFactories.get("r2dbc:h2:mem:///crediya_test;DB_CLOSE_DELAY=-1;");
        }

        @Bean
        ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
            ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
            initializer.setConnectionFactory(connectionFactory);
            initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("db/h2/schema.sql")));
            return initializer;
        }

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapperImp();
        }

        @Bean
        public UserRepositoryAdapter userRepositoryAdapter(UserReactiveRepository userReactiveRepository, ObjectMapper mapper, RoleReactiveRepository roleReactiveRepository) {
            return new UserRepositoryAdapter(userReactiveRepository, mapper, roleReactiveRepository);
        }
    }

    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;

    @Autowired
    private UserReactiveRepository userReactiveRepository;

    @Autowired
    private RoleReactiveRepository roleReactiveRepository;

    private RoleEntity roleEntity;

    private Role role;

    @BeforeEach
    void setUp() {
        userReactiveRepository.deleteAll().block();

        roleEntity = roleReactiveRepository.findByName("CUSTOMER").blockOptional()
                .orElseGet(() -> roleReactiveRepository.save(
                        RoleEntity.builder()
                                .id(UUID.randomUUID())
                                .name("CUSTOMER")
                                .description("Customer with basic access")
                                .build()
                ).block());

        role = new Role(roleEntity.getId(), roleEntity.getName(), roleEntity.getDescription());
    }

    @Test
    void shouldPersistUser_WhenSaveIsCalledWithValidRole() {
        User user = new User(null, "John", "Doe", LocalDate.of(1980, 1, 1), "johndoe@example.com", "Unknown", "123456789", new BigDecimal("15000000"), role);

        StepVerifier.create(userRepositoryAdapter.save(user))
                .assertNext(storedUser -> {
                    Assertions.assertNotNull(storedUser.id());
                    Assertions.assertEquals(user.names(), storedUser.names());
                    Assertions.assertNotNull(storedUser.role());
                    Assertions.assertEquals(roleEntity.getId(), storedUser.role().id());
                    Assertions.assertEquals(roleEntity.getName(), storedUser.role().name());
                }).verifyComplete();

        StepVerifier.create(userRepositoryAdapter.existsByEmail("johndoe@example.com"))
                .assertNext(Assertions::assertTrue).verifyComplete();

        StepVerifier.create(userReactiveRepository.findAll().collectList())
                .assertNext(users -> {
                    Assertions.assertEquals(1, users.size());
                }).verifyComplete();
    }

    @Test
    void shouldThrowRoleNotFoundException_WhenRoleDoesNotExist() {
        Role invalidRole = new Role(null, "INVALID_ROLE", null);
        User user = new User(null, "John", "Doe", LocalDate.of(1980, 1, 1), "johndoe@example.com", "Unknown", "123456789", new BigDecimal("15000000"), invalidRole);

        StepVerifier.create(userRepositoryAdapter.save(user))
                .expectError(RoleNotFoundException.class)
                .verify();
    }
}