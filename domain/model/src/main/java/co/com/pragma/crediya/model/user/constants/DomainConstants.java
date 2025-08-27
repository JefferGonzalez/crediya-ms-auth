package co.com.pragma.crediya.model.user.constants;

import java.math.BigDecimal;

public final class DomainConstants {

    private DomainConstants() {
    }

    public static final String DEFAULT_ROLE = "CUSTOMER";

    public static final BigDecimal MAX_SALARY = new BigDecimal(15000000);

    public static final BigDecimal MIN_SALARY = BigDecimal.ZERO;

    public static final int MIN_LENGTH_NAME = 3;

    public static final int MAX_LENGTH_NAME = 100;

    public static final int MIN_LENGTH_LAST_NAME = 3;

    public static final int MAX_LENGTH_LAST_NAME = 100;

    public static final int MAX_LENGTH_EMAIL = 254;

}