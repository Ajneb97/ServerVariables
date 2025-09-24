package svar.ajneb97.model.structure;

import java.util.List;

public class StringVariable extends Variable{
    private String initialValue;

    public StringVariable(String name, VariableType variableType, ValueType valueType, List<String> possibleValues, Limitations limitations,
                          String initialValue) {
        super(name, variableType, valueType, possibleValues, limitations);
        this.initialValue = initialValue;
    }

    public String getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }
}
