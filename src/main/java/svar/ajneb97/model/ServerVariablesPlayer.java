package svar.ajneb97.model;

import org.bukkit.Bukkit;
import svar.ajneb97.model.structure.ListVariable;
import svar.ajneb97.model.structure.StringVariable;
import svar.ajneb97.model.structure.Variable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ServerVariablesPlayer {

    private UUID uuid;
    private String name;
    private Map<String,ServerVariablesVariable> variables;
    private boolean modified;

    public ServerVariablesPlayer(UUID uuid, String name, Map<String,ServerVariablesVariable> variables) {
        this.uuid = uuid;
        this.name = name;
        this.variables = variables;
        this.modified = false;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, ServerVariablesVariable> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, ServerVariablesVariable> variables) {
        this.variables = variables;
    }

    public void addVariable(ServerVariablesVariable variable){
        variables.put(variable.getVariableName(),variable);
    }

    public ServerVariablesVariable getCurrentVariable(String variableName){
        return variables.get(variableName);
    }

    public String getVariableStringValue(String variableName, StringVariable variable){
        ServerVariablesStringVariable v = (ServerVariablesStringVariable) getCurrentVariable(variableName);
        if(v == null){
            return variable.getInitialValue();
        }
        return v.getCurrentValue();
    }

    public List<String> getVariableListValue(String variableName, ListVariable variable){
        ServerVariablesListVariable v = (ServerVariablesListVariable) getCurrentVariable(variableName);
        if(v == null){
            return variable.getInitialValue();
        }
        return v.getCurrentValue();
    }

    public Object getVariableValue(String variableName, Variable variable){
        ServerVariablesVariable v = getCurrentVariable(variableName);
        if(v == null){
            return variable.getInitialValue();
        }
        return v.getCurrentValue();
    }

    public void setVariableString(String variableName,String newValue){
        ServerVariablesStringVariable v = (ServerVariablesStringVariable) getCurrentVariable(variableName);
        if(v == null){
            v = new ServerVariablesStringVariable(variableName,newValue);
            variables.put(v.getVariableName(),v);
        }else{
            v.setCurrentValue(newValue);
        }
        modified = true;
    }

    public void setVariableList(String variableName,String newValue){
        ServerVariablesStringVariable v = (ServerVariablesStringVariable) getCurrentVariable(variableName);
        if(v == null){
            v = new ServerVariablesStringVariable(variableName,newValue);
            variables.put(v.getVariableName(),v);
        }else{
            v.setCurrentValue(newValue);
        }
        modified = true;
    }

    public ServerVariablesVariable resetVariable(String variableName){
        ServerVariablesVariable removed = variables.remove(variableName);

        if(removed != null){
            modified = true;
        }
        return removed;
    }
}
