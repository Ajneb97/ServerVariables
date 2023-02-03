package svar.ajneb97.managers;

import org.bukkit.configuration.file.FileConfiguration;
import svar.ajneb97.ServerVariables;
import svar.ajneb97.model.VariableResult;
import svar.ajneb97.model.structure.ValueType;
import svar.ajneb97.model.structure.Variable;
import svar.ajneb97.model.structure.VariableType;

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
        if(!variable.getPossibleValues().isEmpty()){
            boolean isPossibleValue = false;
            String possibleValuesText = "";
            List<String> possibleValuesList = variable.getPossibleValues();

            for(int i=0;i<possibleValuesList.size();i++){
                String possibleValue = possibleValuesList.get(i);
                if(possibleValue.equals(newValue)){
                    isPossibleValue = true;
                    break;
                }
                if(i == possibleValuesList.size()-1){
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

        return VariableResult.noErrors(null);
    }
}
