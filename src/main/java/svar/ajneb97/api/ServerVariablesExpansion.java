package svar.ajneb97.api;

import org.bukkit.entity.Player;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import svar.ajneb97.ServerVariables;
import svar.ajneb97.model.StringVariableResult;

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
        StringVariableResult result;
        if(identifier.startsWith("list_")){
            // %servervariables_list_globalvalue_<index>_<variable>%
            // %servervariables_list_globaldisplay_<index>_<variable>%
            // %servervariables_list_value_<index>_<variable>%
            // %servervariables_list_display_<index>_<variable>%
            // %servervariables_list_globallength_<variable>%
            // %servervariables_list_length_<variable>%
            // %servervariables_list_globalcontains_<variable>:<value>%
            // %servervariables_list_contains_<variable>:<value>%
            // %servervariables_list_globalallvalues_<variable>%
            // %servervariables_list_allvalues_<variable>%
            // %servervariables_list_globalalldisplay_<variable>%
            // %servervariables_list_alldisplay_<variable>%
            String identifierM = identifier.replace("list_","");
            String[] sep = identifierM.split("_");
            String subIdentifier = sep[0];

            switch(subIdentifier){
                case "globalvalue": {
                    int index = Integer.parseInt(sep[1]);
                    String variableName = identifierM.substring(identifierM.indexOf(index + "_") + (index + "_").length());
                    result = ServerVariablesAPI.getListVariableValueAtIndex(variableName, index);
                    return result.getResultValue() != null ? result.getResultValue() : result.getErrorKey();
                }
                case "globaldisplay": {
                    int index = Integer.parseInt(sep[1]);
                    String variableName = identifierM.substring(identifierM.indexOf(index + "_") + (index + "_").length());
                    return ServerVariablesAPI.getListVariableDisplayAtIndex(variableName, index);
                }
                case "value": {
                    if (player == null) {
                        return "";
                    }
                    int index = Integer.parseInt(sep[1]);
                    String variableName = identifierM.substring(identifierM.indexOf(index + "_") + (index + "_").length());

                    result = ServerVariablesAPI.getListVariableValueAtIndex(player.getName(), variableName, index);
                    return result.getResultValue() != null ? result.getResultValue() : result.getErrorKey();
                }
                case "display": {
                    if (player == null) {
                        return "";
                    }
                    int index = Integer.parseInt(sep[1]);
                    String variableName = identifierM.substring(identifierM.indexOf(index + "_") + (index + "_").length());
                    return ServerVariablesAPI.getListVariableDisplayAtIndex(player.getName(), variableName, index);
                }
                case "globallength": {
                    String variableName = identifierM.replace("globallength_", "");
                    return ServerVariablesAPI.getListVariableLength(variableName) + "";
                }
                case "length": {
                    if (player == null) {
                        return "";
                    }
                    String variableName = identifierM.replace("length_", "");
                    return ServerVariablesAPI.getListVariableLength(player.getName(), variableName) + "";
                }
                case "globalcontains": {
                    String variableName = identifierM.replace("globalcontains_", "");
                    String[] sep2 = variableName.split(":");
                    return ServerVariablesAPI.listVariableContainsValue(sep2[0], sep2[1]) + "";
                }
                case "contains": {
                    if (player == null) {
                        return "";
                    }
                    String variableName = identifierM.replace("contains_", "");
                    String[] sep2 = variableName.split(":");
                    return ServerVariablesAPI.listVariableContainsValue(player.getName(), sep2[0], sep2[1]) + "";
                }
                case "globalallvalues": {
                    String variableName = identifierM.replace("globalallvalues_", "");
                    return ServerVariablesAPI.getListVariableAllValues(variableName);
                }
                case "allvalues": {
                    if (player == null) {
                        return "";
                    }
                    String variableName = identifierM.replace("allvalues_", "");
                    return ServerVariablesAPI.getListVariableAllValues(player.getName(), variableName);
                }
                case "globalalldisplay": {
                    String variableName = identifierM.replace("globalalldisplay_", "");
                    return ServerVariablesAPI.getListVariableAllValuesDisplay(variableName);
                }
                case "alldisplay": {
                    if (player == null) {
                        return "";
                    }
                    String variableName = identifierM.replace("alldisplay_", "");
                    return ServerVariablesAPI.getListVariableAllValuesDisplay(player.getName(), variableName);
                }
            }
        }else if(identifier.startsWith("globalvalue_")){
        	// %servervariables_globalvalue_<variable>%
        	String variableName = identifier.replace("globalvalue_", "");
            result = ServerVariablesAPI.getVariableValue(variableName);
            return result.getResultValue() != null ? result.getResultValue() : result.getErrorKey();
        }else if(identifier.startsWith("globaldisplay_")){
            // %servervariables_globaldisplay_<variable>%
            String variableName = identifier.replace("globaldisplay_", "");
            return ServerVariablesAPI.getVariableDisplay(variableName);
        }else if(identifier.startsWith("value_otherplayer_")){
            // %servervariables_value_otherplayer_<player>:<variable>%
            String var = identifier.replace("value_otherplayer_", "");
            int index = var.indexOf(":");
            String playerName = var.substring(0,index);
            String variable = var.substring(index+1);
            result = ServerVariablesAPI.getVariableValue(playerName,variable);
            return result.getResultValue() != null ? result.getResultValue() : result.getErrorKey();
        }else if(identifier.startsWith("display_otherplayer_")){
            // %servervariables_display_otherplayer_<player>:<variable>%
            String var = identifier.replace("display_otherplayer_", "");
            int index = var.indexOf(":");
            String playerName = var.substring(0,index);
            String variable = var.substring(index+1);
            return ServerVariablesAPI.getVariableDisplay(playerName,variable);
        }else if(identifier.startsWith("value_")){
            // %servervariables_value_<variable>%
            if(player == null){
                return "";
            }
            String variableName = identifier.replace("value_", "");
            result = ServerVariablesAPI.getVariableValue(player.getName(),variableName);
            return result.getResultValue() != null ? result.getResultValue() : result.getErrorKey();
        }else if(identifier.startsWith("display_")) {
            // %servervariables_display_<variable>%
            if(player == null){
                return "";
            }
            String variableName = identifier.replace("display_", "");
            return ServerVariablesAPI.getVariableDisplay(player.getName(),variableName);
        }else if(identifier.startsWith("initial_value_")){
            // %servervariables_initial_value_<variable>%
            String variableName = identifier.replace("initial_value_", "");
            return ServerVariablesAPI.getStringVariableInitialValue(variableName);
        }

        return null;
    }
}
