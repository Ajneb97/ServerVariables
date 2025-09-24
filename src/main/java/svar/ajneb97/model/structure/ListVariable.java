package svar.ajneb97.model.structure;

import java.util.List;

public class ListVariable extends Variable{

    private List<String> initialValue;
    public ListVariable(String name, VariableType variableType, ValueType valueType, List<String> possibleValues, Limitations limitations,
                        List<String> initialValue) {
        super(name, variableType, valueType, possibleValues, limitations);
        this.initialValue = initialValue;
    }

    public List<String> getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(List<String> initialValue) {
        this.initialValue = initialValue;
    }
}
