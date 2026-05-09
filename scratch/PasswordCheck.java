import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordCheck {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = "$2b$12$3IpxxJtwI/zV8hYwQc8W9eWRkf.l0yocc3Di5FvA65J0QiJ395WSe";
        String password = "admin123";
        boolean matches = encoder.matches(password, hash);
        System.out.println("Matches: " + matches);
    }
}
