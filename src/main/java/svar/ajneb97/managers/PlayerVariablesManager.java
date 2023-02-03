package svar.ajneb97.managers;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import svar.ajneb97.ServerVariables;
import svar.ajneb97.api.VariableChangeEvent;
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
            if(p.getName().equals(name)){
                return p;
            }
        }
        return null;
    }


    public VariableResult modifyVariable(String playerName, String variableName, String value, boolean add){
        FileConfiguration config = plugin.getConfig();
        VariableResult result = getVariableValue(playerName, variableName, true);
        if(result.isError()){
            return VariableResult.error(result.getErrorMessage());
        }

        if(!NumberUtils.isNumber(value)){
            return VariableResult.error(config.getString("messages.invalidValue"));
        }

        if(result.getVariable().getValueType() == ValueType.TEXT){
            return add ? VariableResult.error(config.getString("messages.variableAddError")) :
                    VariableResult.error(config.getString("messages.variableReduceError"));
        }

        try{
            if(value.contains(".")){
                //Double
                double newValue = MathUtils.getDoubleSum(value,result.getResultValue(),add);
                return setVariable(playerName,variableName,newValue+"");
            }else{
                //Integer
                int numericValue = Integer.parseInt(value);
                int newValue = add ? Integer.parseInt(result.getResultValue())+numericValue : Integer.parseInt(result.getResultValue())-numericValue;
                return setVariable(playerName,variableName,newValue+"");
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

        //Check if player is online
        Player player = Bukkit.getPlayer(playerName);
        if(player == null){
            return VariableResult.error(config.getString("messages.playerNotOnline"));
        }

        //Update variable value from existing player (should never return null, because of PlayerListener onJoin())
        ServerVariablesPlayer variablesPlayer = getPlayerByUUID(player.getUniqueId().toString());
        variablesPlayer.setVariable(variableName,newValue);

        plugin.getServer().getPluginManager().callEvent(new VariableChangeEvent(player,variable,newValue));

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

    public VariableResult resetVariable(String playerName, String name){
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

        if(variablesPlayer == null){
            //Never joined the server.
            return VariableResult.noErrorsWithVariable(variable.getInitialValue(),variable);
        }

        variablesPlayer.resetVariable(name);

        plugin.getServer().getPluginManager().callEvent(new VariableChangeEvent(Bukkit.getPlayer(playerName),variable,variable.getInitialValue()));

        return VariableResult.noErrors(null);
    }
}
