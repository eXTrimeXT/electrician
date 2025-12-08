import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TestPassword {
    private final PasswordEncoder passwordEncoder;

    public TestPassword() {
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }

    // Хеширование пароля с использованием BCrypt
    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    // Проверка пароля
    public boolean checkPassword(String inputPassword, String storedHash) {
        return passwordEncoder.matches(inputPassword, storedHash);
    }

    @Test
    public void testCheck(){
        String passwd = "admin";  // $2a$12$85ADATkk12Cv4rjSUWpxg.t4pOOJBWevboxVEzhG8Icfko0vY39nS
        String hash = hashPassword(passwd);
        System.out.println("passwd = " + passwd + "\nhash = " + hash + "\nisCheck = " + checkPassword(passwd, hash));
    }
}