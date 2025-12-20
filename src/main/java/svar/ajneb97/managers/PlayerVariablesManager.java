package svar.ajneb97.managers;

import org.bukkit.entity.Player;
import svar.ajneb97.ServerVariables;
import svar.ajneb97.database.MySQLConnection;
import svar.ajneb97.model.ListVariableResult;
import svar.ajneb97.model.StringVariableResult;
import svar.ajneb97.model.ServerVariablesPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerVariablesManager {

    private final ServerVariables plugin;
    private Map<UUID, ServerVariablesPlayer> playerVariables;
    private final Map<String, UUID> playerNames;

    public PlayerVariablesManager(ServerVariables plugin) {
        this.plugin = plugin;
        this.playerNames = new HashMap<>();
    }

    public Map<UUID, ServerVariablesPlayer> getPlayerVariables() {
        return playerVariables;
    }

    public void setPlayerVariables(Map<UUID, ServerVariablesPlayer> playerVariables) {
        this.playerVariables = playerVariables;
        for (Map.Entry<UUID, ServerVariablesPlayer> entry : playerVariables.entrySet()) {
            playerNames.put(entry.getValue().getName(), entry.getKey());
        }
    }

    public void addPlayer(ServerVariablesPlayer p) {
        playerVariables.put(p.getUuid(), p);
        playerNames.put(p.getName(), p.getUuid());
    }

    private void updatePlayerName(String oldName, String newName, UUID uuid) {
        if (oldName != null) {
            playerNames.remove(oldName);
        }
        playerNames.put(newName, uuid);
    }

    public ServerVariablesPlayer getPlayerByUUID(UUID uuid) {
        return playerVariables.get(uuid);
    }

    private UUID getPlayerUUID(String name) {
        return playerNames.get(name);
    }

    public ServerVariablesPlayer getPlayerByName(String name) {
        UUID uuid = getPlayerUUID(name);
        return playerVariables.get(uuid);
    }

    public void removePlayerByUUID(UUID uuid) {
        playerVariables.remove(uuid);
    }

    // When joining the game
    public void setJoinPlayerData(Player player) {
        if (plugin.getMySQLConnection() != null) {
            MySQLConnection mySQLConnection = plugin.getMySQLConnection();
            UUID uuid = player.getUniqueId();
            mySQLConnection.getPlayer(uuid.toString(), playerData -> {
                removePlayerByUUID(uuid); // Remove data if already exists
                if (playerData != null) {
                    addPlayer(playerData);
                    //Update name if different
                    if (!playerData.getName().equals(player.getName())) {
                        updatePlayerName(playerData.getName(), player.getName(), player.getUniqueId());
                        playerData.setName(player.getName());
                        mySQLConnection.updatePlayerName(playerData);
                    }
                } else {
                    playerData = new ServerVariablesPlayer(uuid, player.getName(), new HashMap<>());
                    addPlayer(playerData);
                    //Create if it doesn't exist
                    mySQLConnection.createPlayer(playerData);
                }
            });
        } else {
            ServerVariablesPlayer playerData = getPlayerByUUID(player.getUniqueId());
            if (playerData != null) {
                // Update name
                if (playerData.getName() == null || !playerData.getName().equals(player.getName())) {
                    updatePlayerName(playerData.getName(), player.getName(), player.getUniqueId());
                    playerData.setName(player.getName());
                    playerData.setModified(true);
                }
            } else {
                // Create empty data for the player
                playerData = new ServerVariablesPlayer(player.getUniqueId(), player.getName(), new HashMap<>());
                playerData.setModified(true);
                addPlayer(playerData);
            }
        }
    }

    public StringVariableResult setVariable(UUID uuid, String variableName, String newValue) {
        ServerVariablesPlayer variablesPlayer = getPlayerByUUID(uuid);
        return plugin.getVariablesManager().setVariableValue(variablesPlayer.getName(), variableName, newValue);
    }

    public StringVariableResult setVariable(String playerName, String variableName, String newValue) {
        return plugin.getVariablesManager().setVariableValue(playerName, variableName, newValue);
    }

    public StringVariableResult getVariableValue(UUID uuid, String variableName, boolean modifying) {
        ServerVariablesPlayer variablesPlayer = getPlayerByUUID(uuid);
        return plugin.getVariablesManager().getVariableValue(variablesPlayer.getName(), variableName, modifying);
    }

    public StringVariableResult getVariableValue(String playerName, String variableName, boolean modifying) {
        return plugin.getVariablesManager().getVariableValue(playerName, variableName, modifying);
    }

    public StringVariableResult setListVariableAtIndex(UUID uuid, String variableName, int index, String newValue) {
        ServerVariablesPlayer variablesPlayer = getPlayerByUUID(uuid);
        return plugin.getVariablesManager().getListVariablesManager().setListVariableValue(variablesPlayer.getName(), variableName, index, newValue, false);
    }

    public StringVariableResult setListVariableAtIndex(String playerName, String variableName, int index, String newValue) {
        return plugin.getVariablesManager().getListVariablesManager().setListVariableValue(playerName, variableName, index, newValue, false);
    }

    public StringVariableResult getListVariableValueAtIndex(UUID uuid, String variableName, int index) {
        ServerVariablesPlayer variablesPlayer = getPlayerByUUID(uuid);
        return plugin.getVariablesManager().getListVariablesManager().getListVariableValueAtIndex(variablesPlayer.getName(), variableName, index);
    }

    public StringVariableResult getListVariableValueAtIndex(String playerName, String variableName, int index) {
        return plugin.getVariablesManager().getListVariablesManager().getListVariableValueAtIndex(playerName, variableName, index);
    }

    public ListVariableResult getListVariableValue(UUID uuid, String variableName) {
        ServerVariablesPlayer variablesPlayer = getPlayerByUUID(uuid);
        return plugin.getVariablesManager().getListVariablesManager().getListVariableValue(variablesPlayer.getName(), variableName, false);
    }

    public ListVariableResult getListVariableValue(String playerName, String variableName) {
        return plugin.getVariablesManager().getListVariablesManager().getListVariableValue(playerName, variableName, false);
    }
}
