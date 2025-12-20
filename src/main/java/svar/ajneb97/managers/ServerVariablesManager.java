package svar.ajneb97.managers;

import svar.ajneb97.model.ServerVariablesVariable;

import java.util.Map;

public class ServerVariablesManager {

    private Map<String, ServerVariablesVariable> variables;

    public Map<String, ServerVariablesVariable> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, ServerVariablesVariable> variables) {
        this.variables = variables;
    }

    public void addVariable(ServerVariablesVariable variable) {
        variables.put(variable.getVariableName(), variable);
    }

    public ServerVariablesVariable getCurrentVariable(String variableName) {
        return variables.get(variableName);
    }
}
