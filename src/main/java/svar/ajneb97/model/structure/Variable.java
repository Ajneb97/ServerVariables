package svar.ajneb97.model.structure;

import java.util.ArrayList;
import java.util.List;

public abstract class Variable {

    private final String name;
    private final VariableType variableType;
    private final ValueType valueType;
    private final List<String> possibleValues;
    private final Limitations limitations;

    public Variable(String name, VariableType variableType, ValueType valueType, List<String> possibleValues, Limitations limitations) {
        this.name = name;
        this.variableType = variableType;
        this.valueType = valueType;
        this.possibleValues = possibleValues;
        this.limitations = limitations;
    }

    public String getName() {
        return name;
    }

    public VariableType getVariableType() {
        return variableType;
    }


    public ValueType getValueType() {
        return valueType;
    }


    public List<String> getPossibleValues() {
        return possibleValues;
    }

    public List<String> getPossibleRealValues() {
        List<String> list = new ArrayList<>();
        for (String value : possibleValues) {
            String realValue = value.split(";")[0];
            list.add(realValue);
        }
        return list;
    }

    public Limitations getLimitations() {
        return limitations;
    }

    public boolean isNumerical() {
        return valueType.equals(ValueType.INTEGER) || valueType.equals(ValueType.DOUBLE);
    }

    public abstract Object getInitialValue();
}
