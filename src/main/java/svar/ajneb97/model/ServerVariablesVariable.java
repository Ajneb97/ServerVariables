package svar.ajneb97.model;

public class ServerVariablesVariable {
    private String variableName;
    private String currentValue;

    public ServerVariablesVariable(String variableName, String currentValue) {
        this.variableName = variableName;
        this.currentValue = currentValue;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(String currentValue) {
        this.currentValue = currentValue;
    }
}
