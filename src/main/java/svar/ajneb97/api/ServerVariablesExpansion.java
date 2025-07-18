package svar.ajneb97.api;

import org.bukkit.entity.Player;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import svar.ajneb97.ServerVariables;

public class ServerVariablesExpansion extends PlaceholderExpansion {

    // We get an instance of the plugin later.
    private ServerVariables plugin;

    public ServerVariablesExpansion(ServerVariables plugin) {
    	this.plugin = plugin;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String getAuthor(){
        return "Ajneb97";
    }

    @Override
    public String getIdentifier(){
        return "servervariables";
    }

    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier){

        if(identifier.startsWith("globalvalue_")){
        	// %servervariables_globalvalue_<variable>%
        	String variableName = identifier.replace("globalvalue_", "");
            return ServerVariablesAPI.getServerVariableValue(variableName);
        }else if(identifier.startsWith("globaldisplay_")){
            // %servervariables_globaldisplay_<variable>%
            String variableName = identifier.replace("globaldisplay_", "");
            return ServerVariablesAPI.getServerVariableDisplay(variableName);
        }else if(identifier.startsWith("value_otherplayer_")){
            // %servervariables_value_otherplayer_<player>:<variable>%
            String var = identifier.replace("value_otherplayer_", "");
            int index = var.indexOf(":");
            String playerName = var.substring(0,index);
            String variable = var.substring(index+1);
            return ServerVariablesAPI.getPlayerVariableValue(playerName,variable);
        }else if(identifier.startsWith("display_otherplayer_")){
            // %servervariables_display_otherplayer_<player>:<variable>%
            String var = identifier.replace("display_otherplayer_", "");
            int index = var.indexOf(":");
            String playerName = var.substring(0,index);
            String variable = var.substring(index+1);
            return ServerVariablesAPI.getPlayerVariableDisplay(playerName,variable);
        }else if(identifier.startsWith("value_")){
            // %servervariables_value_<variable>%
            if(player == null){
                return "";
            }
            String variableName = identifier.replace("value_", "");
            return ServerVariablesAPI.getPlayerVariableValue(player.getName(),variableName);
        }else if(identifier.startsWith("display_")) {
            // %servervariables_display_<variable>%
            if(player == null){
                return "";
            }
            String variableName = identifier.replace("display_", "");
            return ServerVariablesAPI.getPlayerVariableDisplay(player.getName(),variableName);
        }else if(identifier.startsWith("initial_value_")){
            // %servervariables_initial_value_<variable>%
            String variableName = identifier.replace("initial_value_", "");
            return ServerVariablesAPI.getVariableInitialValue(variableName);
        }

        return null;
    }
}
