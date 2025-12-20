package svar.ajneb97.model;

import svar.ajneb97.model.structure.Variable;

public class StringVariableResult extends VariableResult {

    private final String resultValue;
    private int index;

    public StringVariableResult(boolean error, String errorMessage, String resultValue, String errorKey) {
        super(error, errorMessage, errorKey);
        this.resultValue = resultValue;
    }

    public String getResultValue() {
        return resultValue;
    }

    public int getIndex() {
        return index;
    }

    public StringVariableResult withIndex(int index) {
        this.index = index;
        return this;
    }

    public static StringVariableResult noErrors(String resultValue) {
        return new StringVariableResult(false, null, resultValue, null);
    }

    public static StringVariableResult error(String errorMessage, String errorKey) {
        return new StringVariableResult(true, errorMessage, null, errorKey);
    }

    public static StringVariableResult noErrorsWithVariable(String resultValue, Variable variable) {
        StringVariableResult variableResult = new StringVariableResult(false, null, resultValue, null);
        variableResult.setVariable(variable);
        return variableResult;
    }
}
