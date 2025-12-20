package svar.ajneb97.model;

public abstract class ServerVariablesVariable {

    private String variableName;

    public ServerVariablesVariable(String variableName) {
        this.variableName = variableName;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public abstract Object getCurrentValue();
}
