package me.nentify.titles.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.nentify.titles.Utils;
import me.nentify.titles.stats.Stat;
import me.nentify.titles.titles.Title;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MySQLStorage {

    private DataSource db;

    public MySQLStorage(String hostname, int port, String database, String username, String password) {
        SqlService sql = Sponge.getServiceManager().provide(SqlService.class).get();

        String jdbcUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + database;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);

        db = new HikariDataSource(config);

        createTables();
    }

    private Connection getConnection() throws SQLException {
        return db.getConnection();
    }

    private void createTables() {
        try {
            Connection connection = getConnection();

            Statement statement = connection.createStatement();

            statement.execute("CREATE TABLE IF NOT EXISTS titles_players (" +
                    "uuid CHAR(32) NOT NULL, " +
                    "current_title VARCHAR(100) NOT NULL, " +
                    "PRIMARY KEY (id)" +
                    ")");

            statement.execute("CREATE TABLE IF NOT EXISTS titles_stats (" +
                    "player_uuid INT(15) UNSIGNED NOT NULL, " +
                    "stat VARCHAR(100), " +
                    "count INT(15), " +
                    "FOREIGN KEY (player_uuid) REFERENCES titles_players (player_uuid) ON DELETE CASCADE" +
                    ")");

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean userExists(UUID uuid) {
        try {
            Connection connection = getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM titles_players WHERE uuid = ?");
            preparedStatement.setString(1, uuid.toString().replace("-", ""));

            ResultSet resultSet = preparedStatement.executeQuery();

            boolean userExists = resultSet.first();

            connection.close();

            return userExists;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void insertUser(UUID uuid) {
        try {
            Connection connection = getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT " +
                    "INTO titles_players (uuid, current_title) " +
                    "VALUES (?, ?)");
            preparedStatement.setString(1, Utils.uuidToString(uuid));
            preparedStatement.setString(2, Title.Type.ONLINE_TIME.toString());
            preparedStatement.execute();

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStat(UUID uuid, Stat stat, int count) {
        try {
            Connection connection = getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT  " +
                    "INTO titles_stats (player_uuid, stat, count) " +
                    "VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE");
            preparedStatement.setString(1, Utils.uuidToString(uuid));
            preparedStatement.setString(2, stat.toString());
            preparedStatement.setInt(3, count);
            preparedStatement.execute();

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<Stat, Integer> getStatsForPlayer(UUID uuid) {
        try {
            Connection connection = getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT p.*, s.* " +
                    "FROM titles_players AS p " +
                    "INNER JOIN titles_stats AS s ON p.uuid = s.player_uuid " +
                    "WHERE p.uuid = ? ");
            preparedStatement.setString(1, Utils.uuidToString(uuid));

            ResultSet resultSet = preparedStatement.executeQuery();

            Map<Stat, Integer> stats = new HashMap<>();

            while (resultSet.next()) {
                Stat stat = Stat.valueOf(resultSet.getString("stat"));
                int count = resultSet.getInt("count");
                stats.put(stat, count);
            }

            connection.close();

            return stats;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void updateCurrentTitle(UUID uuid, Title.Type type) {
        try {
            Connection connection = getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE titles_players " +
                    "SET current_title = ? " +
                    "WHERE uuid = ? " +
                    "LIMIT 1");
            preparedStatement.setString(1, type.toString());
            preparedStatement.setString(2, Utils.uuidToString(uuid));
            preparedStatement.execute();

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<Title.Type> getCurrentTitleType(UUID uuid) {
        try {
            Connection connection = getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT current_title " +
                    "FROM titles_players " +
                    "WHERE uuid = ? " +
                    "LIMIT 1");
            preparedStatement.setString(1, Utils.uuidToString(uuid));

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.first()) {
                connection.close();
                return Optional.of(Title.Type.ONLINE_TIME);
            }

            Title.Type type = Title.Type.valueOf(resultSet.getString("current_title"));

            connection.close();

            return Optional.of(type);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
