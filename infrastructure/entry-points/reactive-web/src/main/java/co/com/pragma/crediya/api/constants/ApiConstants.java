package co.com.pragma.crediya.api.constants;

import co.com.pragma.crediya.model.user.constants.UserFieldNames;

public final class ApiConstants {

    private ApiConstants() {
    }

    public static final String API_V1 = "/api/v1";

    public static final String LOGIN_PATH = API_V1 + "/login";

    public static final String USERS_PATH = API_V1 + "/users";

    public static final String USERS_SEARCH_PATH = USERS_PATH + "/search";

    public static final String USER_BY_IDENTIFICATION_NUMBER_PATH = USERS_PATH + "/{" + UserFieldNames.IDENTIFICATION_NUMBER + "}";

    public static final String[] PUBLIC_PATTERNS = {
            "/actuator",
            "/actuator/health",
            "/actuator/prometheus",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            LOGIN_PATH + "/**"
    };

    public static final String[] PRIVATE_PATTERNS = {
            USERS_PATH + "/**"
    };

    public static final String BEARER_PREFIX = "Bearer ";

    public static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

}
