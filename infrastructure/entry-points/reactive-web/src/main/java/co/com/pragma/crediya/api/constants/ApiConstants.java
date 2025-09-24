package co.com.pragma.crediya.api.constants;

import co.com.pragma.crediya.model.user.constants.UserFieldNames;

public final class ApiConstants {

    private ApiConstants() {
    }

    public static final String API_V1 = "/api/v1";

    public static final String BASE_PATH = API_V1 + "/auth";

    public static final String LOGIN_PATH = BASE_PATH + "/login";

    public static final String USERS_PATH = BASE_PATH + "/users";

    public static final String USERS_SEARCH_PATH = USERS_PATH + "/search";

    public static final String USER_BY_IDENTIFICATION_NUMBER_PATH = USERS_PATH + "/{" + UserFieldNames.IDENTIFICATION_NUMBER + "}";

    public static final String[] PUBLIC_PATTERNS = {
            BASE_PATH + "/actuator",
            BASE_PATH + "/actuator/health",
            BASE_PATH + "/actuator/prometheus",
            BASE_PATH + "/swagger-ui.html",
            BASE_PATH + "/swagger-ui/**",
            BASE_PATH + "/api-docs/**",
            LOGIN_PATH + "/**"
    };

    public static final String[] PRIVATE_PATTERNS = {
            USERS_PATH + "/**"
    };

    public static final String BEARER_PREFIX = "Bearer ";

    public static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

}
