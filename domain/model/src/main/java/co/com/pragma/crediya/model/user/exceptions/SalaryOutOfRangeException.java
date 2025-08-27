package co.com.pragma.crediya.model.user.exceptions;

public class SalaryOutOfRangeException extends RuntimeException {

    public SalaryOutOfRangeException() {
        super(ErrorMessages.SALARY_OUT_OF_RANGE);
    }

}