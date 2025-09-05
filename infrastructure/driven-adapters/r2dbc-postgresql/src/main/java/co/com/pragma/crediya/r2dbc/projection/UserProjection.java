package co.com.pragma.crediya.r2dbc.projection;

import java.math.BigDecimal;

public record UserProjection(
        String email,
        BigDecimal baseSalary) {
}
