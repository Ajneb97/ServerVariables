package svar.ajneb97.managers;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;
import svar.ajneb97.ServerVariables;
import svar.ajneb97.database.MySQLConnection;
import svar.ajneb97.model.ListVariableResult;
import svar.ajneb97.model.StringVariableResult;
import svar.ajneb97.model.ServerVariablesPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerVariablesManager {
    private ServerVariables plugin;
    private Map<UUID,ServerVariablesPlayer> playerVariables;
    private Map<String,UUID> playerNames;

    public PlayerVariablesManager(ServerVariables plugin) {
        this.plugin = plugin;
        this.playerNames = new HashMap<>();
        this.playerVariables = new HashMap<>();
    }

    public Map<UUID,ServerVariablesPlayer> getPlayerVariables() {
        return playerVariables;
    }

    public void addPlayer(ServerVariablesPlayer p){
        playerVariables.put(p.getUuid(),p);
        playerNames.put(p.getName(), p.getUuid());
    }

    private void updatePlayerName(String oldName,String newName,UUID uuid){
        if(oldName != null){
            playerNames.remove(oldName);
        }
        playerNames.put(newName,uuid);
    }

    public ServerVariablesPlayer getPlayerByUUID(UUID uuid){
        return playerVariables.get(uuid);
    }

    private UUID getPlayerUUID(String name){
        return playerNames.get(name);
    }

    public ServerVariablesPlayer getPlayerByName(String name){
        UUID uuid = getPlayerUUID(name);
        return playerVariables.get(uuid);
    }

    public void removePlayerByUUID(UUID uuid){
        playerVariables.remove(uuid);
    }

    public void removePlayer(ServerVariablesPlayer playerData){
        playerVariables.remove(playerData.getUuid());
        playerNames.remove(playerData.getName());
    }

    // When joining the game
    // Async
    public void manageJoin(AsyncPlayerPreLoginEvent event){
        if(!event.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)){
            return;
        }

        UUID uuid = event.getUniqueId();
        String playerName = event.getName();

        if(plugin.getMySQLConnection() != null) {
            MySQLConnection mySQLConnection = plugin.getMySQLConnection();
            ServerVariablesPlayer playerDataSQL = mySQLConnection.getPlayer(uuid.toString());
            if(playerDataSQL != null) {
                addPlayer(playerDataSQL);
                //Update name if different
                if (!playerDataSQL.getName().equals(playerName)) {
                    updatePlayerName(playerDataSQL.getName(), playerName, uuid);
                    playerDataSQL.setName(playerName);
                    mySQLConnection.updatePlayerName(playerDataSQL);
                }
            }else {
                playerDataSQL = new ServerVariablesPlayer(uuid,playerName,new HashMap<>());
                addPlayer(playerDataSQL);
                //Create if it doesn't exist
                mySQLConnection.createPlayer(playerDataSQL);
            }
        }else{
            ServerVariablesPlayer playerDataFile = plugin.getConfigsManager().getPlayerConfigsManager().loadConfig(uuid);
            if(playerDataFile != null){
                addPlayer(playerDataFile);
                if(playerDataFile.getName() == null || !playerDataFile.getName().equals(playerName)){
                    updatePlayerName(playerDataFile.getName(),playerName,uuid);
                    playerDataFile.setName(playerName);
                    playerDataFile.setModified(true);
                }
            }else{
                //Create empty data for player
                playerDataFile = new ServerVariablesPlayer(uuid,playerName,new HashMap<>());
                playerDataFile.setModified(true);
                addPlayer(playerDataFile);
            }
        }
    }

    public void manageLeave(Player player){
        // Save player data into file and remove from map
        ServerVariablesPlayer playerData = getPlayerByUUID(player.getUniqueId());
        if(playerData != null){
            if(plugin.getMySQLConnection() == null) {
                if(playerData.isModified()){
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            plugin.getConfigsManager().getPlayerConfigsManager().savePlayer(playerData);
                        }
                    }.runTaskAsynchronously(plugin);
                }
            }

            // Retain player data if enabled
            if(!plugin.getConfigsManager().getMainConfigManager().isRetainPlayerDataUntilReset()){
                removePlayer(playerData);
            }
        }
    }

    public StringVariableResult setVariable(UUID uuid, String variableName, String newValue){
        ServerVariablesPlayer variablesPlayer = getPlayerByUUID(uuid);
        return plugin.getVariablesManager().setVariableValue(variablesPlayer.getName(), variableName, newValue);
    }

    public StringVariableResult setVariable(String playerName, String variableName, String newValue){
        return plugin.getVariablesManager().setVariableValue(playerName,variableName,newValue);
    }

    public StringVariableResult getVariableValue(UUID uuid, String variableName, boolean modifying){
        ServerVariablesPlayer variablesPlayer = getPlayerByUUID(uuid);
        return plugin.getVariablesManager().getVariableValue(variablesPlayer.getName(),variableName,modifying);
    }

    public StringVariableResult getVariableValue(String playerName, String variableName, boolean modifying){
        return plugin.getVariablesManager().getVariableValue(playerName,variableName,modifying);
    }

    public StringVariableResult setListVariableAtIndex(UUID uuid, String variableName, int index, String newValue){
        ServerVariablesPlayer variablesPlayer = getPlayerByUUID(uuid);
        return plugin.getVariablesManager().getListVariablesManager().setListVariableValue(variablesPlayer.getName(), variableName, index, newValue, false);
    }

    public StringVariableResult setListVariableAtIndex(String playerName, String variableName, int index, String newValue){
        return plugin.getVariablesManager().getListVariablesManager().setListVariableValue(playerName, variableName, index, newValue, false);
    }

    public StringVariableResult getListVariableValueAtIndex(UUID uuid, String variableName, int index){
        ServerVariablesPlayer variablesPlayer = getPlayerByUUID(uuid);
        return plugin.getVariablesManager().getListVariablesManager().getListVariableValueAtIndex(variablesPlayer.getName(),variableName,index);
    }

    public StringVariableResult getListVariableValueAtIndex(String playerName, String variableName, int index){
        return plugin.getVariablesManager().getListVariablesManager().getListVariableValueAtIndex(playerName,variableName,index);
    }

    public ListVariableResult getListVariableValue(UUID uuid, String variableName){
        ServerVariablesPlayer variablesPlayer = getPlayerByUUID(uuid);
        return plugin.getVariablesManager().getListVariablesManager().getListVariableValue(variablesPlayer.getName(),variableName,false);
    }

    public ListVariableResult getListVariableValue(String playerName, String variableName){
        return plugin.getVariablesManager().getListVariablesManager().getListVariableValue(playerName,variableName,false);
    }

    public StringVariableResult getListVariableValues(UUID uuid, String variableName){
        ServerVariablesPlayer variablesPlayer = getPlayerByUUID(uuid);
        return plugin.getVariablesManager().getListVariablesManager().getListVariableValues(variablesPlayer.getName(),variableName);
    }

    public StringVariableResult getListVariableValues(String playerName, String variableName){
        return plugin.getVariablesManager().getListVariablesManager().getListVariableValues(playerName,variableName);
    }
}
