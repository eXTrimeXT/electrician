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
        String hash = hashPassword(passwd); // $2a$12$A5MUDviotCe1dBVheHCW4OSTQW2dHqxr4uiT51M5MJzxGehkjO2Gy
        System.out.println("passwd = " + passwd + "\nhash = " + hash + "\nisCheck = " + checkPassword(passwd, hash));

        String hash1 = "$2a$12$85ADATkk12Cv4rjSUWpxg.t4pOOJBWevboxVEzhG8Icfko0vY39nS";
        String hash2 = "$2a$12$A5MUDviotCe1dBVheHCW4OSTQW2dHqxr4uiT51M5MJzxGehkjO2Gy";
        String hash3 = "$2a$12$bczoeT3YbJAS1l3rdJ81C.ByoUi8Wdn8XT6MCs6WIEWYc3JGi6Iaa";
        String[] hashes = {hash1, hash2, hash3};
        System.out.println("### EQUALS ###");
        for (String h : hashes) {
            System.out.println(checkPassword(passwd, h));
        }
    }
}