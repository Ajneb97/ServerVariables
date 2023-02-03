package svar.ajneb97.api;

import svar.ajneb97.ServerVariables;
import svar.ajneb97.model.VariableResult;
import svar.ajneb97.model.ServerVariablesPlayer;

public class ServerVariablesAPI {

    private static ServerVariables plugin;
    public ServerVariablesAPI(ServerVariables plugin) {
        this.plugin = plugin;
    }

    public static String getServerVariableValue(String variableName){
        VariableResult result = plugin.getServerVariablesManager().getVariableValue(variableName,false);
        return result.getResultValue();
    }

    public static String getPlayerVariableValue(String playerName, String variableName){
        VariableResult result = plugin.getPlayerVariablesManager().getVariableValue(playerName, variableName, false);
        return result.getResultValue();
    }

    public ServerVariablesPlayer getPlayerByName(String playerName){
        return plugin.getPlayerVariablesManager().getPlayerByName(playerName);
    }

    public ServerVariablesPlayer getPlayerByUUID(String uuid){
        return plugin.getPlayerVariablesManager().getPlayerByUUID(uuid);
    }
}
