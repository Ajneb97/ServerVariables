package svar.ajneb97.model;

import svar.ajneb97.model.structure.Variable;

import java.util.ArrayList;
import java.util.UUID;

public class ServerVariablesPlayer {

    private UUID uuid;
    private String name;
    private ArrayList<ServerVariablesVariable> variables;
    private boolean modified;

    public ServerVariablesPlayer(UUID uuid, String name, ArrayList<ServerVariablesVariable> variables) {
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

    public ArrayList<ServerVariablesVariable> getVariables() {
        return variables;
    }

    public void setVariables(ArrayList<ServerVariablesVariable> variables) {
        this.variables = variables;
    }

    public void addVariable(ServerVariablesVariable variable){
        variables.add(variable);
    }

    public ServerVariablesVariable getVariable(String variableName){
        for(ServerVariablesVariable v : variables){
            if(v.getVariableName().equals(variableName)){
                return v;
            }
        }
        return null;
    }

    public String getVariableValue(String variableName, Variable variable){
        ServerVariablesVariable v = getVariable(variableName);
        if(v == null){
            return variable.getInitialValue();
        }
        return v.getCurrentValue();
    }

    public void setVariable(String variableName,String newValue){
        ServerVariablesVariable v = getVariable(variableName);
        if(v == null){
            v = new ServerVariablesVariable(variableName,newValue);
            variables.add(v);
        }else{
            v.setCurrentValue(newValue);
        }
        modified = true;
    }

    public boolean resetVariable(String variableName){
        for(int i=0;i<variables.size();i++){
            if(variables.get(i).getVariableName().equals(variableName)){
                variables.remove(i);
                modified = true;
                return true;
            }
        }
        return false;
    }
}
