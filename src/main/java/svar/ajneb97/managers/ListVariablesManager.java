package svar.ajneb97.managers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import svar.ajneb97.ServerVariables;
import svar.ajneb97.api.ListVariableChangeEvent;
import svar.ajneb97.model.*;
import svar.ajneb97.model.structure.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListVariablesManager {

    private ServerVariables plugin;

    public ListVariablesManager(ServerVariables plugin){
        this.plugin = plugin;
    }

    public StringVariableResult getListVariableValueAtIndex(String playerName, String variableName, int index){
        FileConfiguration config = plugin.getConfigsManager().getMainConfigManager().getConfig();

        ListVariableResult listVariableResult = getListVariableValue(playerName,variableName,false);
        if(listVariableResult.isError()){
            return listVariableResult.toStringVariableResult();
        }
        List<String> variableValue = listVariableResult.getResultValue();

        // Check if index is out of bounds
        if(index >= variableValue.size()){
            return StringVariableResult.error(config.getString("messages.variableListIndexError")
                    .replace("%index%",index+""),"variableListIndexError");
        }

        return StringVariableResult.noErrorsWithVariable(variableValue.get(index),listVariableResult.getVariable());
    }

    public StringVariableResult setListVariableValue(String playerName, String variableName, int index, String newValue, boolean add){
        FileConfiguration config = plugin.getConfigsManager().getMainConfigManager().getConfig();
        VariablesManager variablesManager = plugin.getVariablesManager();

        // Common verifications.
        StringVariableResult checkCommon = variablesManager.checkVariableCommon(variableName,newValue);
        if(checkCommon.isError()){
            return checkCommon;
        }

        // Verify if resultValue exists.
        if(checkCommon.getResultValue() != null){
            newValue = checkCommon.getResultValue();
        }

        ListVariableResult listVariableResult = getListVariableValue(playerName,variableName,true);
        if(listVariableResult.isError()){
            return listVariableResult.toStringVariableResult();
        }

        List<String> variableValue = listVariableResult.getResultValue();
        Variable variable = listVariableResult.getVariable();
        ServerVariablesListVariable currentVariable = listVariableResult.getCurrentVariable();
        ServerVariablesPlayer serverVariablesPlayer = listVariableResult.getServerVariablesPlayer();
        ServerVariablesManager serverVariablesManager = plugin.getServerVariablesManager();

        List<String> oldValue = new ArrayList<>(variableValue);
        if(!add){
            // Set value at index.
            // Check if index is out of bounds
            if(index >= variableValue.size()){
                return StringVariableResult.error(config.getString("messages.variableListIndexError")
                        .replace("%index%",index+""),"variableListIndexError");
            }

            // Update index with new value.
            variableValue.set(index,newValue);
        }else{
            // Add value to end of list.
            // Update list with new value.
            variableValue.add(newValue);
            index = variableValue.size()-1;
        }

        // Update data.
        Player player = playerName != null ? Bukkit.getPlayer(playerName) : null;
        updateVariableData(playerName,variableName,variableValue,currentVariable,serverVariablesPlayer,serverVariablesManager);

        plugin.getServer().getPluginManager().callEvent(
                new ListVariableChangeEvent(player, variable, variableValue, oldValue, index));

        return StringVariableResult.noErrors(newValue).withIndex(index);
    }

    public ListVariableResult getListVariableValue(String playerName, String variableName, boolean modifying){
        FileConfiguration config = plugin.getConfigsManager().getMainConfigManager().getConfig();

        // Check if variables exists
        Variable aVariable = plugin.getVariablesManager().getVariable(variableName);
        if(aVariable == null){
            return ListVariableResult.error(config.getString("messages.variableDoesNotExists"),"variableDoesNotExists");
        }

        // Check if variable is not LIST
        if(!aVariable.getValueType().equals(ValueType.LIST)){
            return ListVariableResult.error(config.getString("messages.variableNotList"),"variableNotList");
        }

        ListVariable variable = (ListVariable) aVariable;
        ServerVariablesListVariable currentVariable;
        ServerVariablesPlayer serverVariablesPlayer = null;
        if(playerName != null){
            // The variable should be PLAYER type.
            if(variable.getVariableType().equals(VariableType.GLOBAL)){
                if(modifying){
                    return ListVariableResult.error(config.getString("messages.variableSetInvalidTypeGlobal"),"variableSetInvalidTypeGlobal");
                }else{
                    return ListVariableResult.error(config.getString("messages.variableGetInvalidTypeGlobal"),"variableGetInvalidTypeGlobal");
                }
            }

            // Check if player has joined the server.
            serverVariablesPlayer = plugin.getPlayerVariablesManager().getPlayerByName(playerName);
            if(serverVariablesPlayer == null){
                return ListVariableResult.error(config.getString("messages.playerNoData"),"playerNoData");
            }

            currentVariable = (ServerVariablesListVariable) serverVariablesPlayer.getCurrentVariable(variableName);
        }else{
            // The variable should be GLOBAL type.
            if(variable.getVariableType().equals(VariableType.PLAYER)){
                if(modifying){
                    return ListVariableResult.error(config.getString("messages.variableSetInvalidTypePlayer"),"variableSetInvalidTypePlayer");
                }else{
                    return ListVariableResult.error(config.getString("messages.variableGetInvalidTypePlayer"),"variableGetInvalidTypePlayer");
                }
            }

            currentVariable = (ServerVariablesListVariable) plugin.getServerVariablesManager().getCurrentVariable(variableName);
        }

        List<String> variableValue;
        if(currentVariable == null){
            // The variable is not set. Get initial value.
            variableValue = new ArrayList<>(variable.getInitialValue());
        }else{
            variableValue = currentVariable.getCurrentValue();
        }

        return ListVariableResult.noErrorsWithVariable(variableValue,variable).withCurrentVariable(currentVariable)
                .withServerVariablesPlayer(serverVariablesPlayer);
    }

    public StringVariableResult removeListVariableIndex(String playerName, String variableName, int index){
        FileConfiguration config = plugin.getConfigsManager().getMainConfigManager().getConfig();

        ListVariableResult listVariableResult = getListVariableValue(playerName,variableName,true);
        if(listVariableResult.isError()){
            return listVariableResult.toStringVariableResult();
        }

        List<String> variableValue = listVariableResult.getResultValue();
        Variable variable = listVariableResult.getVariable();
        ServerVariablesListVariable currentVariable = listVariableResult.getCurrentVariable();
        ServerVariablesPlayer serverVariablesPlayer = listVariableResult.getServerVariablesPlayer();
        ServerVariablesManager serverVariablesManager = plugin.getServerVariablesManager();

        List<String> oldValue = new ArrayList<>(variableValue);

        if(index >= variableValue.size()){
            return StringVariableResult.error(config.getString("messages.variableListIndexError")
                    .replace("%index%",index+""),"variableListIndexError");
        }

        // Remove index.
        variableValue.remove(index);

        // Update data.
        Player player = playerName != null ? Bukkit.getPlayer(playerName) : null;
        updateVariableData(playerName,variableName,variableValue,currentVariable,serverVariablesPlayer,serverVariablesManager);

        plugin.getServer().getPluginManager().callEvent(
                new ListVariableChangeEvent(player, variable, variableValue, oldValue, index));

        return StringVariableResult.noErrors(null).withIndex(index);
    }

    public StringVariableResult removeListVariableValue(String playerName, String variableName, String value){
        FileConfiguration config = plugin.getConfigsManager().getMainConfigManager().getConfig();

        ListVariableResult listVariableResult = getListVariableValue(playerName,variableName,true);
        if(listVariableResult.isError()){
            return listVariableResult.toStringVariableResult();
        }

        List<String> variableValue = listVariableResult.getResultValue();
        Variable variable = listVariableResult.getVariable();
        ServerVariablesListVariable currentVariable = listVariableResult.getCurrentVariable();
        ServerVariablesPlayer serverVariablesPlayer = listVariableResult.getServerVariablesPlayer();
        ServerVariablesManager serverVariablesManager = plugin.getServerVariablesManager();

        List<String> oldValue = new ArrayList<>(variableValue);

        // Remove index.
        if(!variableValue.remove(value)){
            return StringVariableResult.error(config.getString("messages.variableListValueError")
                    .replace("%value%",value),"variableListValueError");
        }

        // Update data.
        Player player = playerName != null ? Bukkit.getPlayer(playerName) : null;
        updateVariableData(playerName,variableName,variableValue,currentVariable,serverVariablesPlayer,serverVariablesManager);

        plugin.getServer().getPluginManager().callEvent(
                new ListVariableChangeEvent(player, variable, variableValue, oldValue, -1));

        return StringVariableResult.noErrors(null);
    }

    private void updateVariableData(String playerName,String variableName,List<String> variableValue,ServerVariablesListVariable currentVariable,
                                    ServerVariablesPlayer serverVariablesPlayer,ServerVariablesManager serverVariablesManager){
        if(playerName != null){
            if(plugin.getMySQLConnection() != null) {
                plugin.getMySQLConnection().updateVariable(serverVariablesPlayer,variableName,valueFromListToString(variableValue));
            }
            if(currentVariable == null){
                serverVariablesPlayer.addVariable(new ServerVariablesListVariable(variableName,variableValue));
            }else{
                currentVariable.setCurrentValue(variableValue);
            }
            serverVariablesPlayer.setModified(true);
        }else{
            if(currentVariable == null){
                serverVariablesManager.addVariable(new ServerVariablesListVariable(variableName,variableValue));
            }else{
                currentVariable.setCurrentValue(variableValue);
            }
        }
    }

    private String valueFromListToString(List<String> variableValue){
        return String.join("|", variableValue);
    }
}
