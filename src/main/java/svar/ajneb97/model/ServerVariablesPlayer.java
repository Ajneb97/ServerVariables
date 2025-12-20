package svar.ajneb97.model;

import java.util.Map;
import java.util.UUID;

public class ServerVariablesPlayer {

    private final UUID uuid;
    private String name;
    private Map<String, ServerVariablesVariable> variables;
    private boolean modified;

    public ServerVariablesPlayer(UUID uuid, String name, Map<String, ServerVariablesVariable> variables) {
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

    public void addVariable(ServerVariablesVariable variable) {
        variables.put(variable.getVariableName(), variable);
    }

    public ServerVariablesVariable getCurrentVariable(String variableName) {
        return variables.get(variableName);
    }

    public ServerVariablesVariable resetVariable(String variableName) {
        ServerVariablesVariable removed = variables.remove(variableName);

        if (removed != null) {
            modified = true;
        }
        return removed;
    }
}
