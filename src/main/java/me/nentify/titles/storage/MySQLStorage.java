package me.nentify.titles.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.nentify.titles.Titles;
import me.nentify.titles.Utils;
import me.nentify.titles.titles.Title;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * I'VE CREATED A MONSTER!!!!
 */
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
                    "id INT(15) UNSIGNED NOT NULL AUTO_INCREMENT, " +
                    "uuid CHAR(32), " +
                    "PRIMARY KEY (id)" +
                    ")");

            statement.execute("CREATE TABLE IF NOT EXISTS titles_titles (" +
                    "id INT(15) UNSIGNED NOT NULL AUTO_INCREMENT, " +
                    "player_id INT(15) UNSIGNED NOT NULL, " +
                    "type VARCHAR(100), " +
                    "tier VARCHAR(100), " +
                    "PRIMARY KEY (id), " +
                    "FOREIGN KEY (player_id) REFERENCES titles_players (id) ON DELETE CASCADE" +
                    ")");

            statement.execute("CREATE TABLE IF NOT EXISTS titles_current_title (" +
                    "player_id INT(15) UNSIGNED NOT NULL, " +
                    "title_id INT(15) UNSIGNED NOT NULL, " +
                    "FOREIGN KEY (player_id) REFERENCES titles_players (id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (title_id) REFERENCES titles_titles (id) ON DELETE CASCADE" +
                    ")");

            statement.execute("CREATE TABLE IF NOT EXISTS titles_stats (" +
                    "id INT(15) UNSIGNED NOT NULL AUTO_INCREMENT, " +
                    "player_id INT(15) UNSIGNED NOT NULL, " +
                    "stat VARCHAR(100), " +
                    "count INT(15), " +
                    "PRIMARY KEY (id), " +
                    "FOREIGN KEY (player_id) REFERENCES titles_players (id) ON DELETE CASCADE" +
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
                    "INTO titles_players (uuid) " +
                    "values (?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, Utils.uuidToString(uuid));
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                int playerId = resultSet.getInt(1);

                preparedStatement = connection.prepareStatement("INSERT " +
                        "INTO titles_titles (player_id, type, tier)" +
                        "VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1, playerId);
                preparedStatement.setString(2, Title.Type.ONLINE_TIME.toString());
                preparedStatement.setString(3, Title.Tier.NOOB.toString());
                preparedStatement.executeUpdate();

                resultSet = preparedStatement.getGeneratedKeys();

                if (resultSet.next()) {
                    int titleId = resultSet.getInt(1);

                    preparedStatement = connection.prepareStatement("INSERT " +
                            "INTO titles_current_title (player_id, title_id)" +
                            "VALUES (?, ?)");
                    preparedStatement.setInt(1, playerId);
                    preparedStatement.setInt(2, titleId);
                    preparedStatement.execute();
                }
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<Integer> getPlayerId(UUID uuid) {
        try {
            Connection connection = getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * " +
                    "FROM titles_players " +
                    "WHERE uuid = ?");

            preparedStatement.setString(1, Utils.uuidToString(uuid));

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.first()) {
                connection.close();
                return Optional.empty();
            }

            int id = resultSet.getInt("id");

            connection.close();

            return Optional.of(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public Optional<Integer> getTitleId(int playerId, Title.Type type) {
        try {
            Connection connection = getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT id " +
                    "FROM titles_titles " +
                    "WHERE player_id = ? " +
                    "AND type = ?");

            preparedStatement.setInt(1, playerId);
            preparedStatement.setString(2, type.toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.first()) {
                connection.close();
                return Optional.empty();
            }

            int id = resultSet.getInt("id");

            connection.close();

            return Optional.of(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public void insertTitle(UUID uuid, Title.Type type) {
        try {
            Connection connection = getConnection();

            Optional<Integer> playerIdOptional = getPlayerId(uuid);

            if (playerIdOptional.isPresent()) {
                int playerId = playerIdOptional.get();

                PreparedStatement preparedStatement = connection.prepareStatement("INSERT " +
                        "INTO titles_titles (player_id, type, tier) " +
                        "values (?, ?, ?)");

                preparedStatement.setInt(1, playerId);
                preparedStatement.setString(2, type.toString());
                preparedStatement.setString(3, Title.Tier.NOOB.toString());

                preparedStatement.execute();
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTitle(UUID uuid, Title.Type type, Title.Tier tier) {
        try {
            Connection connection = getConnection();

            Optional<Integer> playerIdOptional = getPlayerId(uuid);

            if (playerIdOptional.isPresent()) {
                int playerId = playerIdOptional.get();

                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE titles_titles " +
                        "SET tier = ?" +
                        "WHERE player_id = ? AND type = ?");

                preparedStatement.setString(1, tier.toString());
                preparedStatement.setInt(2, playerId);
                preparedStatement.setString(3, type.toString());

                preparedStatement.execute();
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<Title.Type, Title.Tier> getTitlesForPlayer(UUID uuid) {
        try {
            Connection connection = getConnection();

            Titles.instance.logger.info("hello");

            Optional<Integer> playerIdOptional = getPlayerId(uuid);

            if (playerIdOptional.isPresent()) {
                int playerId = playerIdOptional.get();

                Titles.instance.logger.info(playerId + " ");

                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * " +
                        "FROM titles_titles " +
                        "WHERE player_id = ?");

                preparedStatement.setInt(1, playerId);

                ResultSet resultSet = preparedStatement.executeQuery();

                Map<Title.Type, Title.Tier> titles = new HashMap<>();

                while (resultSet.next()) {
                    Titles.instance.logger.info(resultSet.getString("type") + " " + resultSet.getString("tier"));
                    Title.Type type = Title.Type.valueOf(resultSet.getString("type"));
                    Title.Tier tier = Title.Tier.valueOf(resultSet.getString("tier"));
                    Titles.instance.logger.info(type + " " + tier);
                    titles.put(type, tier);
                }

                return titles;
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void updateCurrentTitle(UUID uuid, Title.Type type) {
        try {
            Connection connection = getConnection();

            Optional<Integer> playerIdOptional = getPlayerId(uuid);

            if (playerIdOptional.isPresent()) {
                int playerId = playerIdOptional.get();

                Optional<Integer> titleIdOptional = getTitleId(playerId, type);

                if (titleIdOptional.isPresent()) {
                    int titleId = titleIdOptional.get();

                    PreparedStatement preparedStatement = connection.prepareStatement("UPDATE titles_current_title " +
                            "SET title_id = ?" +
                            "WHERE player_id = ?");
                    preparedStatement.setInt(1, titleId);
                    preparedStatement.setInt(2, playerId);
                }
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<Title.Type> getCurrentTitleType(UUID uuid) {
        try {
            Connection connection = getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT p.*, c.*, t.* " +
                    "FROM titles_players AS p, " +
                    "INNER JOIN titles_current_title AS c ON p.id = c.player_id " +
                    "INNER JOIN titles_titles AS t ON t.id = c.title_id " +
                    "WHERE p.uuid = ?" +
                    "LIMIT 1");
            preparedStatement.setString(1, Utils.uuidToString(uuid));

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.first()) {
                connection.close();
                return Optional.of(Title.Type.ONLINE_TIME);
            }

            Title.Type type = Title.Type.valueOf(resultSet.getString("type"));

            connection.close();

            return Optional.of(type);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
