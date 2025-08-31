package co.com.pragma.crediya.model.user.exceptions;

import co.com.pragma.crediya.model.user.constants.UserErrorMessages;

public class SalaryOutOfRangeException extends RuntimeException {

    public SalaryOutOfRangeException() {
        super(UserErrorMessages.SALARY_OUT_OF_RANGE);
    }

}