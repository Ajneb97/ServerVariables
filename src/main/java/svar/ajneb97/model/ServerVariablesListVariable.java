package svar.ajneb97.model;

import java.util.List;

public class ServerVariablesListVariable extends ServerVariablesVariable {

    private List<String> currentValue;

    public ServerVariablesListVariable(String variableName, List<String> currentValue) {
        super(variableName);
        this.currentValue = currentValue;
    }

    public List<String> getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(List<String> currentValue) {
        this.currentValue = currentValue;
    }
}
