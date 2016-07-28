//package me.nentify.titles.storage;
//
//import me.nentify.titles.titles.Title;
//import org.spongepowered.api.Sponge;
//import org.spongepowered.api.service.sql.SqlService;
//
//import javax.sql.DataSource;
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.Map;
//import java.util.UUID;
//
//public class MySQLStorage {
//
//    private DataSource dataSource;
//
//    private String jdbcUrl;
//
//    public MySQLStorage(String hostname, int port, String database, String username, String password) {
//        SqlService sql = Sponge.getServiceManager().provide(SqlService.class).get();
//
//        jdbcUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + database + "?user=" + username + "&password=" + password;
//
//        try {
//            dataSource = sql.getDataSource(jdbcUrl);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private Connection getConnection() throws SQLException {
//        return dataSource.getConnection();
//    }
//
//    public void createTables() {
//        try {
//            Connection connection = getConnection();
//
//            Statement statement = connection.createStatement();
//
//            statement.execute("CREATE TABLE IF NOT EXISTS titles_titles (" +
//                    "id INT(15) UNSIGNED AUTO_INCREMENT PRIMARY KEY" +
//                    "uuid VARCHAR()" +
//                    "type VARCHAR(100)" +
//                    "tier VARCHAR(100)" +
//                    ")");
//
//            statement.execute("CREATE TABLE IF NOT EXISTS titles_stats (" +
//                    "id INT(15) UNSIGNED AUTO_INCREMENT PRIMARY KEY" +
//                    "stat VARCHAR(100)" +
//                    "count INT(15)" +
//                    ")");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public Map<Title.Type, Title.Tier> getTitlesForUser(UUID uuid) {
//        try {
//            Connection connection = getConnection();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//}
