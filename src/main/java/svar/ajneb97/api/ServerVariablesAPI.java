package svar.ajneb97.api;

import svar.ajneb97.ServerVariables;
import svar.ajneb97.model.ListVariableResult;
import svar.ajneb97.model.StringVariableResult;
import svar.ajneb97.model.ServerVariablesPlayer;
import svar.ajneb97.model.structure.ListVariable;
import svar.ajneb97.model.structure.StringVariable;
import svar.ajneb97.model.structure.Variable;

import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

public class ServerVariablesAPI {

    private static ServerVariables plugin;
    public ServerVariablesAPI(ServerVariables plugin) {
        this.plugin = plugin;
    }

    public static StringVariableResult getVariableValue(UUID uuid, String variableName){
        return plugin.getPlayerVariablesManager().getVariableValue(uuid, variableName, false);
    }

    public static StringVariableResult getVariableValue(String playerName, String variableName){
        return plugin.getPlayerVariablesManager().getVariableValue(playerName, variableName, false);
    }

    public static StringVariableResult getVariableValue(String variableName){
        return plugin.getVariablesManager().getVariableValue(null, variableName, false);
    }

    public static String getVariableDisplay(String playerName, String variableName){
        StringVariableResult result = plugin.getVariablesManager().getVariableValue(playerName,variableName,false);
        if(result.getVariable() != null){
            return plugin.getVariablesManager().getDisplayFromVariableValue(result.getVariable(),result.getResultValue());
        }
        return result.getResultValue();
    }

    public static String getVariableDisplay(String variableName){
        StringVariableResult result = plugin.getVariablesManager().getVariableValue(null,variableName,false);
        if(result.getVariable() != null){
            return plugin.getVariablesManager().getDisplayFromVariableValue(result.getVariable(),result.getResultValue());
        }
        return result.getResultValue();
    }

    public static StringVariableResult setVariableValue(String variableName, String value){
        return plugin.getVariablesManager().setVariableValue(null,variableName,value);
    }

    public static StringVariableResult setVariableValue(UUID uuid, String variableName, String value){
        return plugin.getPlayerVariablesManager().setVariable(uuid,variableName,value);
    }

    public static StringVariableResult setVariableValue(String playerName, String variableName, String value){
        return plugin.getPlayerVariablesManager().setVariable(playerName,variableName,value);
    }

    public static StringVariableResult getListVariableValueAtIndex(UUID uuid, String variableName, int index){
        return plugin.getPlayerVariablesManager().getListVariableValueAtIndex(uuid, variableName, index);
    }

    public static StringVariableResult getListVariableValueAtIndex(String playerName, String variableName, int index){
        return plugin.getPlayerVariablesManager().getListVariableValueAtIndex(playerName, variableName, index);
    }

    public static StringVariableResult getListVariableValueAtIndex(String variableName, int index){
        return plugin.getVariablesManager().getListVariablesManager().getListVariableValueAtIndex(null, variableName, index);
    }

    public static String getListVariableDisplayAtIndex(String playerName, String variableName, int index){
        StringVariableResult result = plugin.getVariablesManager().getListVariablesManager().getListVariableValueAtIndex(playerName,variableName,index);
        if(result.getVariable() != null){
            return plugin.getVariablesManager().getDisplayFromVariableValue(result.getVariable(),result.getResultValue());
        }
        return result.getResultValue();
    }

    public static String getListVariableDisplayAtIndex(String variableName, int index){
        StringVariableResult result = plugin.getVariablesManager().getListVariablesManager().getListVariableValueAtIndex(null,variableName,index);
        if(result.getVariable() != null){
            return plugin.getVariablesManager().getDisplayFromVariableValue(result.getVariable(),result.getResultValue());
        }
        return result.getResultValue();
    }

    public static StringVariableResult setListVariableValueAtIndex(String variableName, int index, String value){
        return plugin.getVariablesManager().getListVariablesManager().setListVariableValue(null,variableName,index,value,false);
    }

    public static StringVariableResult setListVariableValueAtIndex(UUID uuid, String variableName, int index, String value){
        return plugin.getPlayerVariablesManager().setListVariableAtIndex(uuid,variableName,index,value);
    }

    public static StringVariableResult setListVariableValueAtIndex(String playerName, String variableName, int index, String value){
        return plugin.getPlayerVariablesManager().setListVariableAtIndex(playerName,variableName,index,value);
    }

    public static int getListVariableLength(UUID uuid, String variableName){
        ListVariableResult result = plugin.getPlayerVariablesManager().getListVariableValue(uuid, variableName);
        if(result.isError() || result.getResultValue() == null){
            return 0;
        }
        return result.getResultValue().size();
    }

    public static int getListVariableLength(String playerName, String variableName){
        ListVariableResult result = plugin.getPlayerVariablesManager().getListVariableValue(playerName, variableName);
        if(result.isError() || result.getResultValue() == null){
            return 0;
        }
        return result.getResultValue().size();
    }

    public static int getListVariableLength(String variableName){
        ListVariableResult result = plugin.getVariablesManager().getListVariablesManager().getListVariableValue(null, variableName, false);
        if(result.isError() || result.getResultValue() == null){
            return 0;
        }
        return result.getResultValue().size();
    }

    public static ServerVariablesPlayer getPlayerByName(String playerName){
        return plugin.getPlayerVariablesManager().getPlayerByName(playerName);
    }

    public static ServerVariablesPlayer getPlayerByUUID(UUID uuid){
        return plugin.getPlayerVariablesManager().getPlayerByUUID(uuid);
    }

    public static String getStringVariableInitialValue(String variableName){
        StringVariable variable = (StringVariable) plugin.getVariablesManager().getVariable(variableName);
        if(variable == null){
            return null;
        }
        return variable.getInitialValue();
    }

    public static List<String> getListVariableInitialValue(String variableName){
        ListVariable variable = (ListVariable) plugin.getVariablesManager().getVariable(variableName);
        if(variable == null){
            return null;
        }
        return variable.getInitialValue();
    }

    public static boolean listVariableContainsValue(String playerName, String variableName,String value){
        ListVariableResult result = plugin.getPlayerVariablesManager().getListVariableValue(playerName, variableName);
        if(result.isError() || result.getResultValue() == null){
            return false;
        }
        return result.getResultValue().contains(value);
    }

    public static boolean listVariableContainsValue(String variableName,String value){
        ListVariableResult result = plugin.getVariablesManager().getListVariablesManager().getListVariableValue(null, variableName, false);
        if(result.isError() || result.getResultValue() == null){
            return false;
        }
        return result.getResultValue().contains(value);
    }

    public static String getListVariableAllValues(UUID uuid, String variableName){
        ListVariableResult result = plugin.getPlayerVariablesManager().getListVariableValue(uuid, variableName);
        return joinListValues(result);
    }

    public static String getListVariableAllValues(String playerName, String variableName){
        ListVariableResult result = plugin.getPlayerVariablesManager().getListVariableValue(playerName, variableName);
        return joinListValues(result);
    }

    public static String getListVariableAllValues(String variableName){
        ListVariableResult result = plugin.getVariablesManager().getListVariablesManager().getListVariableValue(null, variableName, false);
        return joinListValues(result);
    }

    public static String getListVariableAllValuesDisplay(UUID uuid, String variableName){
        ListVariableResult result = plugin.getPlayerVariablesManager().getListVariableValue(uuid, variableName);
        return joinListValuesDisplay(result);
    }

    public static String getListVariableAllValuesDisplay(String playerName, String variableName){
        ListVariableResult result = plugin.getPlayerVariablesManager().getListVariableValue(playerName, variableName);
        return joinListValuesDisplay(result);
    }

    public static String getListVariableAllValuesDisplay(String variableName){
        ListVariableResult result = plugin.getVariablesManager().getListVariablesManager().getListVariableValue(null, variableName, false);
        return joinListValuesDisplay(result);
    }

    private static String joinListValues(ListVariableResult result){
        if(result.isError() || result.getResultValue() == null){
            return "";
        }
        List<String> list = result.getResultValue();
        if(list.isEmpty()){
            return "";
        }
        StringJoiner values = new StringJoiner(",");
        for(String value : list){
            values.add(value);
        }
        return values.toString();
    }

    private static String joinListValuesDisplay(ListVariableResult result){
        if(result.isError() || result.getResultValue() == null){
            return "";
        }
        List<String> list = result.getResultValue();
        if(list.isEmpty()){
            return "";
        }
        StringJoiner values = new StringJoiner(",");
        Variable variable = result.getVariable();
        for(String value : list){
            if(variable != null){
                values.add(plugin.getVariablesManager().getDisplayFromVariableValue(variable, value));
            }else{
                values.add(value);
            }
        }
        return values.toString();
    }

    public static ServerVariables getPlugin() {
        return plugin;
    }
}
