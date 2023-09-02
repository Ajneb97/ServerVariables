package svar.ajneb97.managers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import svar.ajneb97.ServerVariables;
import svar.ajneb97.model.VariableResult;
import svar.ajneb97.model.structure.Limitations;
import svar.ajneb97.model.structure.ValueType;
import svar.ajneb97.model.structure.Variable;

import java.util.ArrayList;
import java.util.List;

public class VariablesManager {
    private ServerVariables plugin;
    private ArrayList<Variable> variables;

    public VariablesManager(ServerVariables plugin) {
        this.plugin = plugin;
    }

    public ArrayList<Variable> getVariables() {
        return variables;
    }

    public void setVariables(ArrayList<Variable> variables) {
        this.variables = variables;
    }

    public Variable getVariable(String name){
        for(Variable v : variables) {
            if(v.getName().equals(name)){
                return v;
            }
        }
        return null;
    }

    public VariableResult checkVariableCommon(String variableName, String newValue){
        FileConfiguration config = plugin.getConfig();
        Variable variable = plugin.getVariablesManager().getVariable(variableName);
        if(variable == null){
            //Variable doesn't exist
            return VariableResult.error(config.getString("messages.variableDoesNotExists"));
        }

        //Check if newValue is valid
        ValueType type = variable.getValueType();
        if(!ValueType.isValid(type,newValue)){
            return VariableResult.error(config.getString("messages.variableInvalidValue")
                    .replace("%value_type%",type.name()));
        }

        //Check for possible values
        List<String> possibleRealValues = variable.getPossibleRealValues();
        if(!possibleRealValues.isEmpty()){
            boolean isPossibleValue = false;
            String possibleValuesText = "";

            for(int i=0;i<possibleRealValues.size();i++){
                String possibleValue = possibleRealValues.get(i);
                if(possibleValue.equals(newValue)){
                    isPossibleValue = true;
                    break;
                }
                if(i == possibleRealValues.size()-1){
                    possibleValuesText = possibleValuesText+possibleValue;
                }else{
                    possibleValuesText = possibleValuesText+possibleValue+", ";
                }

            }
            if(!isPossibleValue){
                return VariableResult.error(config.getString("messages.variableNotPossibleValue")
                        .replace("%values%",possibleValuesText));
            }
        }

        //Check limitations
        Limitations limitations = variable.getLimitations();
        if(variable.isNumerical()){
            double value = Double.parseDouble(newValue);
            String maxValue;
            String minValue;
            if(variable.getValueType().equals(ValueType.DOUBLE)){
                maxValue = limitations.getMaxValue()+"";
                minValue = limitations.getMinValue()+"";
            }else{
                maxValue = (int)limitations.getMaxValue()+"";
                minValue = (int)limitations.getMinValue()+"";
            }
            if(value > limitations.getMaxValue()){
                return VariableResult.error(config.getString("messages.variableLimitationOutOfRangeMax")
                        .replace("%value%",maxValue));
            }
            if(value < limitations.getMinValue()){
                return VariableResult.error(config.getString("messages.variableLimitationOutOfRangeMin")
                        .replace("%value%",minValue));
            }
        }

        return VariableResult.noErrors(null);
    }

    public String getDisplayFromVariableValue(Variable variable,String value){
        List<String> possibleValues = variable.getPossibleValues();

        for(String possibleValue : possibleValues){
            String[] fullValue = possibleValue.split(";");
            if(fullValue[0].equals(value)){
                if(fullValue.length > 1){
                    return fullValue[1];
                }
            }
        }

        return value;
    }
}
