package svar.ajneb97.model.structure;

import java.util.ArrayList;
import java.util.List;

public class Variable {

    private String name;
    private VariableType variableType;
    private ValueType valueType;
    private String initialValue;
    private List<String> possibleValues;
    private Limitations limitations;

    public Variable(String name,VariableType variableType, ValueType valueType, String initialValue
    ,List<String> possibleValues,Limitations limitations) {
        this.name = name;
        this.variableType = variableType;
        this.valueType = valueType;
        this.initialValue = initialValue;
        this.possibleValues = possibleValues;
        this.limitations = limitations;
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

    public List<String> getPossibleValues(){
        return possibleValues;
    }
    public List<String> getPossibleRealValues(){
        List<String> list = new ArrayList<>();
        for(String value : possibleValues){
            String realValue = value.split(";")[0];
            list.add(realValue);
        }
        return list;
    }

    public Limitations getLimitations() {
        return limitations;
    }

    public void setLimitations(Limitations limitations) {
        this.limitations = limitations;
    }

    public boolean isNumerical(){
        return valueType.equals(ValueType.INTEGER) || valueType.equals(ValueType.DOUBLE);
    }
}
