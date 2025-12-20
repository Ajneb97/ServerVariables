package svar.ajneb97.model;

import svar.ajneb97.model.structure.Variable;

public abstract class VariableResult {

    protected boolean error;
    protected String errorMessage;
    protected String errorKey;
    protected Variable variable;

    public VariableResult(boolean error, String errorMessage, String errorKey) {
        this.error = error;
        this.errorMessage = errorMessage;
        this.errorKey = errorKey;
    }

    public boolean isError() {
        return error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Variable getVariable() {
        return variable;
    }

    public void setVariable(Variable variable) {
        this.variable = variable;
    }

    public String getErrorKey() {
        return errorKey;
    }
}
