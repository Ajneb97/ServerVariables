package svar.ajneb97.model;

import svar.ajneb97.model.structure.Variable;

public class VariableResult {
    private boolean error;
    private String errorMessage;
    private String resultValue;
    private Variable variable; //Not always present

    public VariableResult(boolean error, String errorMessage, String resultValue) {
        this.error = error;
        this.errorMessage = errorMessage;
        this.resultValue = resultValue;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getResultValue() {
        return resultValue;
    }

    public Variable getVariable() {
        return variable;
    }

    public void setVariable(Variable variable) {
        this.variable = variable;
    }

    public static VariableResult noErrors(String resultValue){
        return new VariableResult(false, null, resultValue);
    }

    public static VariableResult error(String errorMessage){
        return new VariableResult(true, errorMessage, null);
    }

    public static VariableResult noErrorsWithVariable(String resultValue,Variable variable){
        VariableResult variableResult = new VariableResult(false, null, resultValue);
        variableResult.setVariable(variable);
        return variableResult;
    }
}
