package config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseConnection {
    // Thông tin kết nối CSDL
    private static final String URL = "jdbc:mysql://localhost:3306/hethongchungcu";
    private static final String USERNAME = "root";
    private static final String PASSWORD = ""; //
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    
    private static Connection connection = null;
    
    // Kết nối đơn (Singleton Pattern)
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName(DRIVER);
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("✓ Kết nối CSDL thành công!");
            } catch (ClassNotFoundException e) {
                System.out.println("✗ Driver MySQL không tìm thấy!");
                throw e;
            } catch (SQLException e) {
                System.out.println("✗ Kết nối CSDL thất bại!");
                throw e;
            }
        }
        return connection;
    }
    
    // Đóng kết nối
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Đóng kết nối CSDL thành công!");
            }
        } catch (SQLException e) {
            System.out.println("✗ Lỗi khi đóng kết nối!");
            e.printStackTrace();
        }
    }
    
    // Kiểm tra kết nối
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException | ClassNotFoundException e) {
            return false;
        }
    }
}