package svar.ajneb97.managers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import svar.ajneb97.ServerVariables;
import svar.ajneb97.api.ListVariableChangeEvent;
import svar.ajneb97.api.StringVariableChangeEvent;
import svar.ajneb97.model.*;
import svar.ajneb97.model.structure.*;
import svar.ajneb97.utils.MathUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("DataFlowIssue")
public class VariablesManager {

    private final ServerVariables plugin;
    private final ListVariablesManager listVariablesManager;
    private Map<String, Variable> variables;

    public VariablesManager(ServerVariables plugin) {
        this.plugin = plugin;
        this.listVariablesManager = new ListVariablesManager(plugin);
    }

    public Map<String, Variable> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Variable> variables) {
        this.variables = variables;
    }

    public Variable getVariable(String name) {
        return variables.get(name);
    }

    public ListVariablesManager getListVariablesManager() {
        return listVariablesManager;
    }

    public StringVariableResult getVariableValue(String playerName, @NotNull String variableName, boolean modifying) {
        FileConfiguration config = plugin.getConfigsManager().getMainConfigManager().getConfig();

        Variable aVariable = plugin.getVariablesManager().getVariable(variableName);
        if (aVariable == null) {
            return StringVariableResult.error(config.getString("messages.variableDoesNotExists"), "variableDoesNotExists");
        }

        // Check if variable is LIST
        if (aVariable.getValueType().equals(ValueType.LIST)) {
            return StringVariableResult.error(config.getString("messages.variableIsList"), "variableIsList");
        }

        ServerVariablesStringVariable currentVariable;
        if (playerName != null) {
            // The variable should be a PLAYER type.
            if (aVariable.getVariableType().equals(VariableType.GLOBAL)) {
                if (modifying) {
                    return StringVariableResult.error(config.getString("messages.variableSetInvalidTypeGlobal"), "variableSetInvalidTypeGlobal");
                } else {
                    return StringVariableResult.error(config.getString("messages.variableGetInvalidTypeGlobal"), "variableGetInvalidTypeGlobal");
                }
            }

            // Check if the player has joined the server.
            ServerVariablesPlayer serverVariablesPlayer = plugin.getPlayerVariablesManager().getPlayerByName(playerName);
            if (serverVariablesPlayer == null) {
                return StringVariableResult.error(config.getString("messages.playerNoData"), "playerNoData");
            }

            currentVariable = (ServerVariablesStringVariable) serverVariablesPlayer.getCurrentVariable(variableName);
        } else {
            // The variable should be a GLOBAL type.
            if (aVariable.getVariableType().equals(VariableType.PLAYER)) {
                if (modifying) {
                    return StringVariableResult.error(config.getString("messages.variableSetInvalidTypePlayer"), "variableSetInvalidTypePlayer");
                } else {
                    return StringVariableResult.error(config.getString("messages.variableGetInvalidTypePlayer"), "variableGetInvalidTypePlayer");
                }
            }

            currentVariable = (ServerVariablesStringVariable) plugin.getServerVariablesManager().getCurrentVariable(variableName);
        }

        StringVariable variable = (StringVariable) aVariable;
        if (currentVariable == null) {
            // Get initial value
            return StringVariableResult.noErrorsWithVariable(variable.getInitialValue(), variable);
        }
        return StringVariableResult.noErrorsWithVariable(currentVariable.getCurrentValue(), variable);
    }

    public StringVariableResult setVariableValue(String playerName, @NotNull String variableName, @NotNull String newValue) {
        FileConfiguration config = plugin.getConfigsManager().getMainConfigManager().getConfig();
        VariablesManager variablesManager = plugin.getVariablesManager();

        StringVariableResult checkCommon = variablesManager.checkVariableCommon(variableName, newValue);
        if (checkCommon.isError()) {
            return checkCommon;
        }

        // Verify if resultValue exists
        if (checkCommon.getResultValue() != null) {
            newValue = checkCommon.getResultValue();
        }

        // Check if variable is LIST
        Variable aVariable = checkCommon.getVariable();
        if (aVariable.getValueType().equals(ValueType.LIST)) {
            return StringVariableResult.error(config.getString("messages.variableIsList"), "variableIsList");
        }

        ServerVariablesManager serverVariablesManager = plugin.getServerVariablesManager();
        StringVariable variable = (StringVariable) aVariable;
        ServerVariablesStringVariable currentVariable;
        ServerVariablesPlayer serverVariablesPlayer = null;
        if (playerName != null) {
            // The variable should be a PLAYER type.
            if (variable.getVariableType().equals(VariableType.GLOBAL)) {
                return StringVariableResult.error(config.getString("messages.variableSetInvalidTypeGlobal"), "variableSetInvalidTypeGlobal");
            }

            // Check if the player has joined the server.
            serverVariablesPlayer = plugin.getPlayerVariablesManager().getPlayerByName(playerName);
            if (serverVariablesPlayer == null) {
                return StringVariableResult.error(config.getString("messages.playerNoData"), "playerNoData");
            }

            currentVariable = (ServerVariablesStringVariable) serverVariablesPlayer.getCurrentVariable(variableName);
        } else {
            // The variable should be a GLOBAL type.
            if (variable.getVariableType().equals(VariableType.PLAYER)) {
                return StringVariableResult.error(config.getString("messages.variableSetInvalidTypePlayer"), "variableSetInvalidTypePlayer");
            }

            currentVariable = (ServerVariablesStringVariable) serverVariablesManager.getCurrentVariable(variableName);
        }

        // Transformations
        newValue = variablesManager.variableTransformations(variable, newValue);

        String oldValue;
        if (currentVariable == null) {
            // The variable is not set. Get initial value.
            oldValue = variable.getInitialValue();
        } else {
            oldValue = currentVariable.getCurrentValue();
        }

        // Update data
        Player player = null;
        if (playerName != null) {
            if (plugin.getMySQLConnection() != null) {
                plugin.getMySQLConnection().updateVariable(serverVariablesPlayer, variableName, newValue);
            }

            if (currentVariable == null) {
                serverVariablesPlayer.addVariable(new ServerVariablesStringVariable(variableName, newValue));
            } else {
                currentVariable.setCurrentValue(newValue);
            }
            serverVariablesPlayer.setModified(true);

            player = Bukkit.getPlayer(serverVariablesPlayer.getName());
        } else {
            if (currentVariable == null) {
                serverVariablesManager.addVariable(new ServerVariablesStringVariable(variableName, newValue));
            } else {
                currentVariable.setCurrentValue(newValue);
            }
        }

        plugin.getServer().getPluginManager().callEvent(
                new StringVariableChangeEvent(player, variable, newValue, oldValue));

        return StringVariableResult.noErrors(newValue);
    }

    public StringVariableResult modifyVariable(String playerName, String variableName, String value, boolean add) {
        FileConfiguration config = plugin.getConfigsManager().getMainConfigManager().getConfig();

        // Get current value
        StringVariableResult result;
        if (playerName != null) {
            // Player
            result = getVariableValue(playerName, variableName, true);
        } else {
            // Global
            result = getVariableValue(null, variableName, true);
        }

        if (result.isError()) {
            return StringVariableResult.error(result.getErrorMessage(), result.getErrorKey());
        }

        //Value must be a number
        if (!MathUtils.isParsable(value)) {
            return StringVariableResult.error(config.getString("messages.invalidValue"), "invalidValue");
        }

        //ValueType must not be TEXT
        ValueType valueType = result.getVariable().getValueType();
        if (valueType == ValueType.TEXT) {
            return add ? StringVariableResult.error(config.getString("messages.variableAddError"), "variableAddError") :
                    StringVariableResult.error(config.getString("messages.variableReduceError"), "variableReduceError");
        }

        try {
            double newValue = MathUtils.getDoubleSum(value, result.getResultValue(), add);
            if (value.contains(".") || valueType == ValueType.DOUBLE) {
                return setVariableValue(playerName, variableName, newValue + "");
            } else {
                return setVariableValue(playerName, variableName, ((long) newValue) + "");
            }
        } catch (NumberFormatException e) {
            return add ? StringVariableResult.error(config.getString("messages.variableAddError"), "variableAddError") :
                    StringVariableResult.error(config.getString("messages.variableReduceError"), "variableReduceError");
        }
    }

    public StringVariableResult resetVariable(String playerName, String variableName, boolean allPlayers) {
        FileConfiguration config = plugin.getConfigsManager().getMainConfigManager().getConfig();

        Variable aVariable = plugin.getVariablesManager().getVariable(variableName);
        if (aVariable == null) {
            return StringVariableResult.error(config.getString("messages.variableDoesNotExists"), "variableDoesNotExists");
        }

        if (playerName != null || allPlayers) {
            // The variable should be a PLAYER type.
            if (aVariable.getVariableType().equals(VariableType.GLOBAL)) {
                return StringVariableResult.error(config.getString("messages.variableResetInvalidTypeGlobal"), "variableResetInvalidTypeGlobal");
            }

            // Check if the player has joined the server.
            ServerVariablesPlayer serverVariablesPlayer = null;
            if (!allPlayers) {
                serverVariablesPlayer = plugin.getPlayerVariablesManager().getPlayerByName(playerName);
                if (serverVariablesPlayer == null) {
                    return StringVariableResult.error(config.getString("messages.playerNoData"), "playerNoData");
                }
            }

            if (plugin.getMySQLConnection() != null) {
                if (allPlayers) {
                    plugin.getMySQLConnection().resetVariable(null, variableName, true);
                } else {
                    plugin.getMySQLConnection().resetVariable(serverVariablesPlayer, variableName, false);
                }
            }

            if (allPlayers) {
                Map<UUID, ServerVariablesPlayer> playerVariables = plugin.getPlayerVariablesManager().getPlayerVariables();
                for (Map.Entry<UUID, ServerVariablesPlayer> entry : playerVariables.entrySet()) {
                    ServerVariablesPlayer p = entry.getValue();
                    ServerVariablesVariable removed = p.resetVariable(variableName);
                    if (removed != null && p.getName() != null) {
                        Player player = Bukkit.getPlayer(p.getName());
                        if (player != null) {
                            if (aVariable.getValueType().equals(ValueType.LIST)) {
                                ServerVariablesListVariable removedF = (ServerVariablesListVariable) removed;
                                ListVariable variable = (ListVariable) aVariable;
                                plugin.getServer().getPluginManager().callEvent(
                                        new ListVariableChangeEvent(player, variable, variable.getInitialValue(), removedF.getCurrentValue(), -1));
                            } else {
                                ServerVariablesStringVariable removedF = (ServerVariablesStringVariable) removed;
                                StringVariable variable = (StringVariable) aVariable;
                                plugin.getServer().getPluginManager().callEvent(
                                        new StringVariableChangeEvent(player, variable, variable.getInitialValue(), removedF.getCurrentValue()));
                            }
                        }
                    }
                }
            } else {
                if (aVariable.getValueType().equals(ValueType.LIST)) {
                    ServerVariablesListVariable removed = (ServerVariablesListVariable) serverVariablesPlayer.resetVariable(variableName);
                    if (removed != null) {
                        ListVariable variable = (ListVariable) aVariable;
                        plugin.getServer().getPluginManager().callEvent(
                                new ListVariableChangeEvent(Bukkit.getPlayer(playerName), variable, variable.getInitialValue(), removed.getCurrentValue(), -1));
                    }
                } else {
                    ServerVariablesStringVariable removed = (ServerVariablesStringVariable) serverVariablesPlayer.resetVariable(variableName);
                    if (removed != null) {
                        StringVariable variable = (StringVariable) aVariable;
                        plugin.getServer().getPluginManager().callEvent(
                                new StringVariableChangeEvent(Bukkit.getPlayer(playerName), variable, variable.getInitialValue(), removed.getCurrentValue()));
                    }
                }
            }
        } else {
            // The variable should be a GLOBAL type.
            if (aVariable.getVariableType().equals(VariableType.PLAYER)) {
                return StringVariableResult.error(config.getString("messages.variableResetInvalidTypePlayer"), "variableResetInvalidTypePlayer");
            }

            if (aVariable.getValueType().equals(ValueType.LIST)) {
                ServerVariablesListVariable removed = (ServerVariablesListVariable) plugin.getServerVariablesManager().getVariables().remove(variableName);
                if (removed != null) {
                    ListVariable variable = (ListVariable) aVariable;
                    plugin.getServer().getPluginManager().callEvent(
                            new ListVariableChangeEvent(null, variable, variable.getInitialValue(), removed.getCurrentValue(), -1));
                }
            } else {
                ServerVariablesStringVariable removed = (ServerVariablesStringVariable) plugin.getServerVariablesManager().getVariables().remove(variableName);
                if (removed != null) {
                    StringVariable variable = (StringVariable) aVariable;
                    plugin.getServer().getPluginManager().callEvent(
                            new StringVariableChangeEvent(null, variable, variable.getInitialValue(), removed.getCurrentValue()));
                }
            }
        }

        return StringVariableResult.noErrors(null);
    }

    public StringVariableResult checkVariableCommon(String variableName, String newValue) {
        FileConfiguration config = plugin.getConfigsManager().getMainConfigManager().getConfig();
        Variable variable = plugin.getVariablesManager().getVariable(variableName);
        if (variable == null) {
            //Variable doesn't exist
            return StringVariableResult.error(config.getString("messages.variableDoesNotExists"), "variableDoesNotExists");
        }

        //Check if newValue is valid
        ValueType type = variable.getValueType();
        if (!ValueType.isValid(type, newValue)) {
            return StringVariableResult.error(config.getString("messages.variableInvalidValue")
                    .replace("%value_type%", type.name()), "variableInvalidValue");
        }

        //Check for possible values
        List<String> possibleRealValues = variable.getPossibleRealValues();
        if (!possibleRealValues.isEmpty()) {
            boolean isPossibleValue = false;
            StringBuilder possibleValuesText = new StringBuilder();

            for (int i = 0; i < possibleRealValues.size(); i++) {
                String possibleValue = possibleRealValues.get(i);
                if (possibleValue.equals(newValue)) {
                    isPossibleValue = true;
                    break;
                }
                if (i == possibleRealValues.size() - 1) {
                    possibleValuesText.append(possibleValue);
                } else {
                    possibleValuesText.append(possibleValue).append(", ");
                }

            }
            if (!isPossibleValue) {
                return StringVariableResult.error(config.getString("messages.variableNotPossibleValue")
                        .replace("%values%", possibleValuesText.toString()), "variableNotPossibleValue");
            }
        }

        String resultValue = null;

        //Check limitations
        Limitations limitations = variable.getLimitations();
        if (variable.isNumerical()) {
            double value = Double.parseDouble(newValue);
            String maxValue;
            String minValue;
            if (variable.getValueType().equals(ValueType.DOUBLE)) {
                maxValue = limitations.getMaxValue() + "";
                minValue = limitations.getMinValue() + "";
            } else {
                maxValue = (long) limitations.getMaxValue() + "";
                minValue = (long) limitations.getMinValue() + "";
            }
            if (value > limitations.getMaxValue()) {
                if (!limitations.isManageOutOfRange()) {
                    return StringVariableResult.error(config.getString("messages.variableLimitationOutOfRangeMax")
                            .replace("%value%", maxValue), "variableLimitationOutOfRangeMax");
                }
                resultValue = maxValue;
            }
            if (value < limitations.getMinValue()) {
                if (!limitations.isManageOutOfRange()) {
                    return StringVariableResult.error(config.getString("messages.variableLimitationOutOfRangeMin")
                            .replace("%value%", minValue), "variableLimitationOutOfRangeMin");
                }
                resultValue = minValue;
            }
        } else {
            int maxCharacters = limitations.getMaxCharacters();
            if (newValue.length() > maxCharacters) {
                return StringVariableResult.error(config.getString("messages.variableLimitationMaxCharactersError")
                        .replace("%value%", maxCharacters + ""), "variableLimitationMaxCharactersError");
            }
        }

        return StringVariableResult.noErrorsWithVariable(resultValue, variable);
    }

    public String variableTransformations(Variable variable, String newValue) {
        if (!variable.getValueType().equals(ValueType.DOUBLE)) {
            return newValue;
        }
        int maxDecimals = variable.getLimitations().getMaxDecimals();

        return new BigDecimal(newValue).setScale(maxDecimals, RoundingMode.HALF_UP).toString();
    }

    public String getDisplayFromVariableValue(Variable variable, String value) {
        List<String> possibleValues = variable.getPossibleValues();

        for (String possibleValue : possibleValues) {
            String[] fullValue = possibleValue.split(";");
            if (fullValue[0].equals(value)) {
                if (fullValue.length > 1) {
                    return fullValue[1];
                }
            }
        }

        return value;
    }


}
