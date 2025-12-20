package svar.ajneb97.database;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import svar.ajneb97.ServerVariables;
import svar.ajneb97.managers.MessagesManager;
import svar.ajneb97.managers.VariablesManager;
import svar.ajneb97.model.ServerVariablesListVariable;
import svar.ajneb97.model.ServerVariablesPlayer;
import svar.ajneb97.model.ServerVariablesStringVariable;
import svar.ajneb97.model.ServerVariablesVariable;
import svar.ajneb97.model.structure.ValueType;
import svar.ajneb97.model.structure.Variable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySQLConnection {

    private final ServerVariables plugin;
    private final Logger logger;
    private final String prefix = ServerVariables.prefix;
    private HikariConnection connection;
    private final boolean isFolia;

    public MySQLConnection(ServerVariables plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.isFolia = plugin.isFolia;
    }

    public void setupMySql() {
        FileConfiguration config = plugin.getConfigsManager().getMainConfigManager().getConfigFile().getConfig();
        try {
            connection = new HikariConnection(config);
            connection.getHikari().getConnection();
            createTables();
            loadData();
            logger.info(MessagesManager.getLegacyColoredMessage(prefix + " &aSuccessfully connected to the Database."));
        } catch (Exception e) {
            logger.severe(MessagesManager.getLegacyColoredMessage(prefix + " &cError while connecting to the Database."));
        }
    }

    public Connection getConnection() {
        try {
            return connection.getHikari().getConnection();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting database connection", e);
            return null;
        }
    }

    public void loadData() {
        Map<UUID, ServerVariablesPlayer> playerMap = new HashMap<>();
        VariablesManager variablesManager = plugin.getVariablesManager();
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT servervariables_players.UUID, servervariables_players.PLAYER_NAME, " +
                            "servervariables_players_variables.NAME, " +
                            "servervariables_players_variables.VALUE " +
                            "FROM servervariables_players LEFT JOIN servervariables_players_variables " +
                            "ON servervariables_players.UUID = servervariables_players_variables.UUID");

            ResultSet result = statement.executeQuery();
            while (result.next()) {
                UUID uuid = UUID.fromString(result.getString("UUID"));
                String playerName = result.getString("PLAYER_NAME");
                String variableName = result.getString("NAME");
                String variableValue = result.getString("VALUE");

                ServerVariablesPlayer player = playerMap.computeIfAbsent(uuid, u -> new ServerVariablesPlayer(u, playerName, new HashMap<>()));

                if (variableName != null && variableValue != null) {
                    Variable variable = variablesManager.getVariable(variableName);
                    if (variable == null) {
                        continue;
                    }

                    if (variable.getValueType().equals(ValueType.LIST)) {
                        player.addVariable(new ServerVariablesListVariable(variableName, new ArrayList<>(Arrays.asList(variableValue.split("\\|")))));
                    } else {
                        player.addVariable(new ServerVariablesStringVariable(variableName, variableValue));
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error loading data from MySQL", e);
        }

        plugin.getPlayerVariablesManager().setPlayerVariables(playerMap);
    }

    public void createTables() {
        try (Connection connection = getConnection()) {
            PreparedStatement statement1 = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS servervariables_players" +
                            " (UUID varchar(36) NOT NULL, " +
                            " PLAYER_NAME varchar(50), " +
                            " PRIMARY KEY ( UUID ))"
            );
            statement1.executeUpdate();
            PreparedStatement statement2 = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS servervariables_players_variables" +
                            " (ID int NOT NULL AUTO_INCREMENT, " +
                            " UUID varchar(36) NOT NULL, " +
                            " NAME varchar(100), " +
                            " VALUE text, " +
                            " PRIMARY KEY ( ID ), " +
                            " FOREIGN KEY (UUID) REFERENCES servervariables_players(UUID))");
            statement2.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating MySQL tables", e);
        }
    }

    public void getPlayer(String uuid, PlayerCallback callback) {
        Runnable runnable = () -> {
            VariablesManager variablesManager = plugin.getVariablesManager();
            ServerVariablesPlayer player = null;
            try (Connection connection = getConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT servervariables_players.UUID, servervariables_players.PLAYER_NAME, " +
                                "servervariables_players_variables.NAME, " +
                                "servervariables_players_variables.VALUE " +
                                "FROM servervariables_players LEFT JOIN servervariables_players_variables " +
                                "ON servervariables_players.UUID = servervariables_players_variables.UUID " +
                                "WHERE servervariables_players.UUID = ?");

                statement.setString(1, uuid);
                ResultSet result = statement.executeQuery();

                boolean firstFind = true;
                while (result.next()) {
                    String playerName = result.getString("PLAYER_NAME");
                    String variableName = result.getString("NAME");
                    String variableValue = result.getString("VALUE");
                    if (firstFind) {
                        firstFind = false;
                        player = new ServerVariablesPlayer(UUID.fromString(uuid), playerName, new HashMap<>());
                    }
                    if (variableName != null && variableValue != null) {
                        Variable variable = variablesManager.getVariable(variableName);
                        if (variable == null) {
                            continue;
                        }

                        if (variable.getValueType().equals(ValueType.LIST)) {
                            player.addVariable(new ServerVariablesListVariable(variableName, new ArrayList<>(Arrays.asList(variableValue.split("\\|")))));
                        } else {
                            player.addVariable(new ServerVariablesStringVariable(variableName, variableValue));
                        }
                    }
                }

                ServerVariablesPlayer finalPlayer = player;
                if (isFolia) {
                    plugin.getServer().getGlobalRegionScheduler().execute(plugin, () -> callback.onDone(finalPlayer));
                } else {
                    Bukkit.getScheduler().runTask(plugin, () -> callback.onDone(finalPlayer));
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error getting player data from MySQL: " + uuid, e);
            }
        };

        if (isFolia) {
            plugin.getServer().getAsyncScheduler().runNow(plugin, t -> runnable.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
        }
    }

    public void createPlayer(ServerVariablesPlayer player) {
        Runnable runnable = () -> {
            try (Connection connection = getConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO servervariables_players " +
                                "(UUID, PLAYER_NAME) VALUE (?,?)");

                statement.setString(1, player.getUuid().toString());
                statement.setString(2, player.getName());
                statement.executeUpdate();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error creating player in MySQL: " + player.getName(), e);
            }
        };

        if (isFolia) {
            plugin.getServer().getAsyncScheduler().runNow(plugin, t -> runnable.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
        }
    }

    public void updatePlayerName(ServerVariablesPlayer player) {
        Runnable runnable = () -> {
            try (Connection connection = getConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE servervariables_players SET " +
                                "PLAYER_NAME=? WHERE UUID=?");

                statement.setString(1, player.getName());
                statement.setString(2, player.getUuid().toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error updating player name in MySQL: " + player.getName(), e);
            }
        };

        if (isFolia) {
            plugin.getServer().getAsyncScheduler().runNow(plugin, t -> runnable.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
        }
    }

    public void updateVariable(ServerVariablesPlayer player, String variable, String value) {
        ServerVariablesVariable v = player.getCurrentVariable(variable);
        Runnable runnable = () -> {
            try (Connection connection = getConnection()) {
                PreparedStatement statement;

                if (v == null) {
                    // Insert
                    statement = connection.prepareStatement(
                            "INSERT INTO servervariables_players_variables " +
                                    "(UUID, NAME, VALUE) VALUE (?,?,?)");

                    statement.setString(1, player.getUuid().toString());
                    statement.setString(2, variable);
                    statement.setString(3, value);
                } else {
                    // Update
                    statement = connection.prepareStatement(
                            "UPDATE servervariables_players_variables SET " +
                                    "VALUE=? WHERE UUID=? AND NAME=?");

                    statement.setString(1, value);
                    statement.setString(2, player.getUuid().toString());
                    statement.setString(3, variable);
                }

                statement.executeUpdate();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error updating variable '" + variable + "' for player: " + player.getName(), e);
            }
        };

        if (isFolia) {
            plugin.getServer().getAsyncScheduler().runNow(plugin, t -> runnable.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
        }
    }

    public void resetVariable(ServerVariablesPlayer player, String variable, boolean all) {
        Runnable runnable = () -> {
            try (Connection connection = getConnection()) {
                PreparedStatement statement;
                if (all) {
                    statement = connection.prepareStatement(
                            "DELETE FROM servervariables_players_variables " +
                                    "WHERE NAME=?");
                    statement.setString(1, variable);
                } else {
                    statement = connection.prepareStatement(
                            "DELETE FROM servervariables_players_variables " +
                                    "WHERE UUID=? AND NAME=?");
                    statement.setString(1, player.getUuid().toString());
                    statement.setString(2, variable);
                }
                statement.executeUpdate();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error resetting variable '" + variable + "' in MySQL", e);
            }
        };

        if (isFolia) {
            plugin.getServer().getAsyncScheduler().runNow(plugin, t -> runnable.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
        }
    }

    public void disable() {
        if (connection != null) {
            connection.disable();
        }
    }
}
