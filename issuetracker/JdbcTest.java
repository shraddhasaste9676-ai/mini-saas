import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

public class JdbcTest {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://127.0.0.1:3306/mydb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String user = "root";
        String password = "469676";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("Enabling log_bin_trust_function_creators:");
            try {
                stmt.execute("SET GLOBAL log_bin_trust_function_creators = 1");
                System.out.println("Enabled successfully.");
                
                System.out.println("Granting SUPER to appuser:");
                stmt.execute("GRANT ALL PRIVILEGES ON mydb.* TO 'appuser'@'localhost'");
                System.out.println("Granted successfully.");
            } catch (SQLException e) {
                System.err.println("Failed:");
                e.printStackTrace();
            }
        }
    }
}
