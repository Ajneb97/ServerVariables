package svar.ajneb97.managers;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import svar.ajneb97.ServerVariables;
import svar.ajneb97.api.VariableChangeEvent;
import svar.ajneb97.database.MySQLConnection;
import svar.ajneb97.model.VariableResult;
import svar.ajneb97.model.ServerVariablesPlayer;
import svar.ajneb97.model.ServerVariablesVariable;
import svar.ajneb97.model.structure.ValueType;
import svar.ajneb97.model.structure.Variable;
import svar.ajneb97.model.structure.VariableType;
import svar.ajneb97.utils.MathUtils;

import java.util.ArrayList;

public class PlayerVariablesManager {
    private ServerVariables plugin;
    private ArrayList<ServerVariablesPlayer> playerVariables;

    public PlayerVariablesManager(ServerVariables plugin) {
        this.plugin = plugin;
    }

    public ArrayList<ServerVariablesPlayer> getPlayerVariables() {
        return playerVariables;
    }

    public void setPlayerVariables(ArrayList<ServerVariablesPlayer> playerVariables) {
        this.playerVariables = playerVariables;
    }

    public void addPlayer(ServerVariablesPlayer p){
        playerVariables.add(p);
    }

    public ServerVariablesPlayer getPlayerByUUID(String uuid){
        for(ServerVariablesPlayer p : playerVariables){
            if(p.getUuid().equals(uuid)){
                return p;
            }
        }
        return null;
    }

    public ServerVariablesPlayer getPlayerByName(String name){
        for(ServerVariablesPlayer p : playerVariables){
            if(p.getName() != null && p.getName().equals(name)){
                return p;
            }
        }
        return null;
    }

    public void removePlayerByUUID(String uuid){
        for(int i=0;i<playerVariables.size();i++){
            if(playerVariables.get(i).getUuid().equals(uuid)){
                playerVariables.remove(i);
                return;
            }
        }
    }

    //When joining the game
    public void setJoinPlayerData(Player player){
        if(plugin.getMySQLConnection() != null){
            MySQLConnection mySQLConnection = plugin.getMySQLConnection();
            String uuid = player.getUniqueId().toString();
            mySQLConnection.getPlayer(uuid, playerData -> {
                removePlayerByUUID(uuid); //Remove data if already exists
                if(playerData != null) {
                    addPlayer(playerData);
                    //Update name if different
                    if(!playerData.getName().equals(player.getName())){
                        playerData.setName(player.getName());
                        mySQLConnection.updatePlayerName(playerData);
                    }
                }else {
                    playerData = new ServerVariablesPlayer(uuid,player.getName(),new ArrayList<>());
                    addPlayer(playerData);
                    //Create if it doesn't exist
                    mySQLConnection.createPlayer(playerData);
                }
            });
        }else{
            ServerVariablesPlayer p = getPlayerByUUID(player.getUniqueId().toString());
            if(p != null){
                //Update name
                if(p.getName() == null || !p.getName().equals(player.getName())){
                    p.setName(player.getName());
                    p.setModified(true);
                }
            }else{
                //Create empty data for player
                p = new ServerVariablesPlayer(player.getUniqueId().toString(),player.getName(),new ArrayList<>());
                p.setModified(true);
                playerVariables.add(p);
            }
        }
    }

    public VariableResult modifyVariable(String playerName, String variableName, String value, boolean add){
        FileConfiguration config = plugin.getConfig();
        VariableResult result = getVariableValue(playerName, variableName, true);
        if(result.isError()){
            return VariableResult.error(result.getErrorMessage());
        }

        //Value must be a number
        if(!NumberUtils.isNumber(value)){
            return VariableResult.error(config.getString("messages.invalidValue"));
        }

        //ValueType must not be TEXT
        ValueType valueType = result.getVariable().getValueType();
        if(valueType == ValueType.TEXT){
            return add ? VariableResult.error(config.getString("messages.variableAddError")) :
                    VariableResult.error(config.getString("messages.variableReduceError"));
        }

        try{
            double newValue = MathUtils.getDoubleSum(value,result.getResultValue(),add);
            if(value.contains(".") || valueType == ValueType.DOUBLE){
                return setVariable(playerName,variableName,newValue+"");
            }else{
                return setVariable(playerName,variableName,((int)newValue)+"");
            }
        }catch(NumberFormatException e){
            return add ? VariableResult.error(config.getString("messages.variableAddError")) :
                    VariableResult.error(config.getString("messages.variableReduceError"));
        }
    }


    public VariableResult setVariable(String playerName, String variableName, String newValue){
        FileConfiguration config = plugin.getConfig();
        VariablesManager variablesManager = plugin.getVariablesManager();
        Variable variable = variablesManager.getVariable(variableName);
        VariableResult checkCommon = variablesManager.checkVariableCommon(variableName,newValue);
        if(checkCommon.isError()){
            return checkCommon;
        }

        //If newValue is null, setting variable with initial value
        if(newValue == null){
            newValue = variable.getInitialValue();
        }

        //Check if type is truly PLAYER
        if(variable.getVariableType().equals(VariableType.GLOBAL)){
            return VariableResult.error(config.getString("messages.variableSetInvalidTypeGlobal"));
        }

        ServerVariablesPlayer variablesPlayer = getPlayerByName(playerName);
        if(variablesPlayer == null){
            //The player hasn't joined the server and can't set data.
            return VariableResult.error(config.getString("messages.playerNoData"));
        }

        if(plugin.getMySQLConnection() != null) {
            plugin.getMySQLConnection().updateVariable(variablesPlayer,variableName,newValue);
        }
        variablesPlayer.setVariable(variableName,newValue);

        plugin.getServer().getPluginManager().callEvent(new VariableChangeEvent(Bukkit.getPlayer(playerName),variable,newValue));

        return VariableResult.noErrors(newValue);
    }

    public VariableResult getVariableValue(String playerName, String name, boolean modifying){
        FileConfiguration config = plugin.getConfig();

        ServerVariablesPlayer variablesPlayer = getPlayerByName(playerName);
        Variable variable = plugin.getVariablesManager().getVariable(name);

        if(variable == null){
            return VariableResult.error(config.getString("messages.variableDoesNotExists"));
        }

        //Check if type is truly PLAYER
        if(variable.getVariableType().equals(VariableType.GLOBAL)){
            if(modifying){
                return VariableResult.error(config.getString("messages.variableSetInvalidTypeGlobal"));
            }else{
                return VariableResult.error(config.getString("messages.variableGetInvalidTypeGlobal"));
            }
        }

        if(variablesPlayer == null){
            //Never joined the server.
            return VariableResult.error(config.getString("messages.playerNoData"));
        }

        ServerVariablesVariable currentVariable = variablesPlayer.getVariable(name);
        if(currentVariable == null){
            //Get initial value (player has data, but not the variable itself)
            return VariableResult.noErrorsWithVariable(variable.getInitialValue(),variable);
        }
        return VariableResult.noErrorsWithVariable(currentVariable.getCurrentValue(),variable);
    }

    public VariableResult resetVariable(String playerName, String name, boolean all){
        FileConfiguration config = plugin.getConfig();

        ServerVariablesPlayer variablesPlayer = getPlayerByName(playerName);
        Variable variable = plugin.getVariablesManager().getVariable(name);

        if(variable == null){
            return VariableResult.error(config.getString("messages.variableDoesNotExists"));
        }

        //Check if type is truly PLAYER
        if(variable.getVariableType().equals(VariableType.GLOBAL)){
            return VariableResult.error(config.getString("messages.variableResetInvalidTypeGlobal"));
        }

        if(variablesPlayer == null && !all){
            //Never joined the server.
            return VariableResult.noErrorsWithVariable(variable.getInitialValue(),variable);
        }

        if(plugin.getMySQLConnection() != null) {
            if(all){
                plugin.getMySQLConnection().resetVariable(null,name,true);
            }else{
                plugin.getMySQLConnection().resetVariable(variablesPlayer,name,false);
            }
        }

        if(all){
            for(ServerVariablesPlayer p : playerVariables){
                if(p.resetVariable(name) && p.getName() != null){
                    plugin.getServer().getPluginManager().callEvent(new VariableChangeEvent(Bukkit.getPlayer(p.getName()),variable,variable.getInitialValue()));
                }
            }
        }else{
            if(variablesPlayer.resetVariable(name)){
                plugin.getServer().getPluginManager().callEvent(new VariableChangeEvent(Bukkit.getPlayer(playerName),variable,variable.getInitialValue()));
            }
        }

        return VariableResult.noErrors(null);
    }
}
