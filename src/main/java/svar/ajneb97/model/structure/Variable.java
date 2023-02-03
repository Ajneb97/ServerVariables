package svar.ajneb97.model.structure;

import java.util.List;

public class Variable {

    private String name;
    private VariableType variableType;
    private ValueType valueType;
    private String initialValue;
    private List<String> possibleValues;

    public Variable(String name,VariableType variableType, ValueType valueType, String initialValue
    ,List<String> possibleValues) {
        this.name = name;
        this.variableType = variableType;
        this.valueType = valueType;
        this.initialValue = initialValue;
        this.possibleValues = possibleValues;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VariableType getVariableType() {
        return variableType;
    }

    public void setVariableType(VariableType variableType) {
        this.variableType = variableType;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public String getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }

    public List<String> getPossibleValues() {
        return possibleValues;
    }

    public void setPossibleValues(List<String> possibleValues) {
        this.possibleValues = possibleValues;
    }
}
