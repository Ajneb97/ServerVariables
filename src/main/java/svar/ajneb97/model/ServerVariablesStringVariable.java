package svar.ajneb97.model;

public class ServerVariablesStringVariable extends ServerVariablesVariable{

    private String currentValue;

    public ServerVariablesStringVariable(String variableName, String currentValue) {
        super(variableName);
        this.currentValue = currentValue;
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(String currentValue) {
        this.currentValue = currentValue;
    }
}
