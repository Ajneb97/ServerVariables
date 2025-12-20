package svar.ajneb97.api;

import org.bukkit.entity.Player;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;
import svar.ajneb97.ServerVariables;
import svar.ajneb97.model.StringVariableResult;

@SuppressWarnings("deprecation")
public class ServerVariablesExpansion extends PlaceholderExpansion {

    // We get an instance of the plugin later.
    private final ServerVariables plugin;

    public ServerVariablesExpansion(ServerVariables plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getAuthor() {
        return "Ajneb97";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "servervariables";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        StringVariableResult result;
        if (identifier.startsWith("list_")) {
            // %servervariables_list_globalvalue_<index>_<variable>%
            // %servervariables_list_globaldisplay_<index>_<variable>%
            // %servervariables_list_value_<index>_<variable>%
            // %servervariables_list_display_<index>_<variable>%
            // %servervariables_list_globallength_<variable>%
            // %servervariables_list_length_<variable>%
            // %servervariables_list_globalcontains_<variable>:<value>%
            // %servervariables_list_contains_<variable>:<value>%
            String identifierM = identifier.replace("list_", "");
            String[] sep = identifierM.split("_");
            String subIdentifier = sep[0];

            switch (subIdentifier) {
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
            }
        } else if (identifier.startsWith("globalvalue_")) {
            // %servervariables_globalvalue_<variable>%
            String variableName = identifier.replace("globalvalue_", "");
            result = ServerVariablesAPI.getVariableValue(variableName);
            return result.getResultValue() != null ? result.getResultValue() : result.getErrorKey();
        } else if (identifier.startsWith("globaldisplay_")) {
            // %servervariables_globaldisplay_<variable>%
            String variableName = identifier.replace("globaldisplay_", "");
            return ServerVariablesAPI.getVariableDisplay(variableName);
        } else if (identifier.startsWith("value_otherplayer_")) {
            // %servervariables_value_otherplayer_<player>:<variable>%
            String var = identifier.replace("value_otherplayer_", "");
            int index = var.indexOf(":");
            String playerName = var.substring(0, index);
            String variable = var.substring(index + 1);
            result = ServerVariablesAPI.getVariableValue(playerName, variable);
            return result.getResultValue() != null ? result.getResultValue() : result.getErrorKey();
        } else if (identifier.startsWith("display_otherplayer_")) {
            // %servervariables_display_otherplayer_<player>:<variable>%
            String var = identifier.replace("display_otherplayer_", "");
            int index = var.indexOf(":");
            String playerName = var.substring(0, index);
            String variable = var.substring(index + 1);
            return ServerVariablesAPI.getVariableDisplay(playerName, variable);
        } else if (identifier.startsWith("value_")) {
            // %servervariables_value_<variable>%
            if (player == null) {
                return "";
            }
            String variableName = identifier.replace("value_", "");
            result = ServerVariablesAPI.getVariableValue(player.getName(), variableName);
            return result.getResultValue() != null ? result.getResultValue() : result.getErrorKey();
        } else if (identifier.startsWith("display_")) {
            // %servervariables_display_<variable>%
            if (player == null) {
                return "";
            }
            String variableName = identifier.replace("display_", "");
            return ServerVariablesAPI.getVariableDisplay(player.getName(), variableName);
        } else if (identifier.startsWith("initial_value_")) {
            // %servervariables_initial_value_<variable>%
            String variableName = identifier.replace("initial_value_", "");
            return ServerVariablesAPI.getStringVariableInitialValue(variableName);
        } else if (identifier.startsWith("predict_display_")) {
            String[] split = identifier.replace("predict_display_", "").split(":");
            if (split.length < 2) {
                return "";
            } else {
                String variableName = split[0];
                String future = split[1];
                return this.plugin.getVariablesManager().getDisplayFromVariableValue(this.plugin.getVariablesManager().getVariable(variableName), future);
            }
        }

        return null;
    }
}
