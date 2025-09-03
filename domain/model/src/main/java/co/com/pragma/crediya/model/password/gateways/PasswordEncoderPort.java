package co.com.pragma.crediya.model.password.gateways;

public interface PasswordEncoderPort {

    String encode(String raw);

    boolean matches(String raw, String encoded);

}
