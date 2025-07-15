package svar.ajneb97.managers;

import org.bukkit.configuration.file.FileConfiguration;
import svar.ajneb97.ServerVariables;
import svar.ajneb97.api.VariableChangeEvent;
import svar.ajneb97.model.VariableResult;
import svar.ajneb97.model.ServerVariablesVariable;
import svar.ajneb97.model.structure.ValueType;
import svar.ajneb97.model.structure.Variable;
import svar.ajneb97.model.structure.VariableType;
import svar.ajneb97.utils.MathUtils;

import java.util.ArrayList;

public class ServerVariablesManager {
    private ServerVariables plugin;
    private ArrayList<ServerVariablesVariable> variables;

    public ServerVariablesManager(ServerVariables plugin) {
        this.plugin = plugin;
        clearVariables();
    }

    public void clearVariables(){
        this.variables = new ArrayList<ServerVariablesVariable>();
    }

    public ArrayList<ServerVariablesVariable> getVariables() {
        return variables;
    }

    public void addVariable(String name,String value){
        variables.add(new ServerVariablesVariable(name,value));
    }


    public ServerVariablesVariable getCurrentVariable(String name){
        for(ServerVariablesVariable v : variables){
            if(v.getVariableName().equals(name)){
                return v;
            }
        }
        return null;
    }

    public VariableResult modifyVariable(String variableName, String value, boolean add){
        FileConfiguration config = plugin.getConfig();
        VariableResult result = getVariableValue(variableName, true);
        if(result.isError()){
            return VariableResult.error(result.getErrorMessage());
        }

        if(!MathUtils.isParsable(value)){
            return VariableResult.error(config.getString("messages.invalidValue"));
        }

        if(result.getVariable().getValueType() == ValueType.TEXT){
            return add ? VariableResult.error(config.getString("messages.variableAddError")) :
                    VariableResult.error(config.getString("messages.variableReduceError"));
        }

        try{
            if(value.contains(".")){
                //Double
                double newValue = MathUtils.getDoubleSum(value, result.getResultValue(), add);
                return setVariable(variableName,newValue+"");
            }else{
                //Integer
                long numericValue = Long.parseLong(value);
                long newValue = add ? Long.parseLong(result.getResultValue())+numericValue : Long.parseLong(result.getResultValue())-numericValue;
                return setVariable(variableName,newValue+"");
            }
        }catch(NumberFormatException e){
            return add ? VariableResult.error(config.getString("messages.variableAddError")) :
                    VariableResult.error(config.getString("messages.variableReduceError"));
        }
    }

    public VariableResult setVariable(String variableName, String newValue){
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

        //Check if type is truly GLOBAL
        if(variable.getVariableType().equals(VariableType.PLAYER)){
            return VariableResult.error(config.getString("messages.variableSetInvalidTypePlayer"));
        }

        ServerVariablesVariable currentVariable = getCurrentVariable(variable.getName());

        // Transformations
        newValue = variablesManager.variableTransformations(variable,newValue);

        //If not exists, create it
        String oldValue;
        if(currentVariable == null){
            oldValue = variable.getInitialValue();
            currentVariable = new ServerVariablesVariable(variableName,newValue);
            variables.add(currentVariable);
        }else{
            oldValue = currentVariable.getCurrentValue();
            currentVariable.setCurrentValue(newValue);
        }

        plugin.getServer().getPluginManager().callEvent(new VariableChangeEvent(null, variable, newValue, oldValue));

        return VariableResult.noErrors(newValue);
    }

    public VariableResult getVariableValue(String name,boolean modifying){
        FileConfiguration config = plugin.getConfig();
        ServerVariablesVariable currentVariable = getCurrentVariable(name);

        Variable variable = plugin.getVariablesManager().getVariable(name);

        if(variable == null){
            return VariableResult.error(config.getString("messages.variableDoesNotExists"));
        }

        //Check if type is truly GLOBAL
        if(variable.getVariableType().equals(VariableType.PLAYER)){
            if(modifying){
                return VariableResult.error(config.getString("messages.variableSetInvalidTypePlayer"));
            }else{
                return VariableResult.error(config.getString("messages.variableGetInvalidTypePlayer"));
            }
        }

        if(currentVariable == null){
            //Check for initial value
            return VariableResult.noErrorsWithVariable(variable.getInitialValue(),variable);
        }
        return VariableResult.noErrorsWithVariable(currentVariable.getCurrentValue(),variable);
    }

    public VariableResult resetVariable(String name){
        FileConfiguration config = plugin.getConfig();

        Variable variable = plugin.getVariablesManager().getVariable(name);

        if(variable == null){
            return VariableResult.error(config.getString("messages.variableDoesNotExists"));
        }

        //Check if type is truly GLOBAL
        if(variable.getVariableType().equals(VariableType.PLAYER)){
            return VariableResult.error(config.getString("messages.variableResetInvalidTypePlayer"));
        }

        String oldValue = variable.getInitialValue();
        for(int i=0;i<variables.size();i++){
            if(variables.get(i).getVariableName().equals(name)){
                oldValue = variables.get(i).getCurrentValue();
                variables.remove(i);
                break;
            }
        }

        plugin.getServer().getPluginManager().callEvent(new VariableChangeEvent(null,variable,variable.getInitialValue(),oldValue));

        return VariableResult.noErrors(null);
    }
}
