import java.sql.*;
import java.io.FileInputStream;
import java.util.Properties;

public class InsertAdmin {
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.load(new FileInputStream(".env.properties"));
        String url = props.getProperty("DATABASE_URL");
        String user = props.getProperty("DATABASE_USERNAME");
        String pass = props.getProperty("DATABASE_PASSWORD");

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement()) {
            String hash = "$2a$12$KQPsSr66IHC0B81dcVfut.k8ccMNVV7NfxmDYp5wstM9p0gutfa0G";
            String sql = "INSERT INTO admin_users (username, password_hash, active) VALUES ('thaian', '" + hash + "', true) ON CONFLICT (username) DO NOTHING";
            int rows = stmt.executeUpdate(sql);
            System.out.println(rows > 0 ? "Created admin user: thaian" : "User already exists");
        }
    }
}
