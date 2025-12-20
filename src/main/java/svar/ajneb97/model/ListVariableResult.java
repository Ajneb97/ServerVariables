package svar.ajneb97.model;

import svar.ajneb97.model.structure.Variable;

import java.util.List;

public class ListVariableResult extends VariableResult {

    private final List<String> resultValue;
    private ServerVariablesListVariable currentVariable;
    private ServerVariablesPlayer serverVariablesPlayer;

    public ListVariableResult(boolean error, String errorMessage, List<String> resultValue, String errorKey) {
        super(error, errorMessage, errorKey);
        this.resultValue = resultValue;
    }

    public List<String> getResultValue() {
        return resultValue;
    }

    public static ListVariableResult error(String errorMessage, String errorKey) {
        return new ListVariableResult(true, errorMessage, null, errorKey);
    }

    public static ListVariableResult noErrorsWithVariable(List<String> resultValue, Variable variable) {
        ListVariableResult variableResult = new ListVariableResult(false, null, resultValue, null);
        variableResult.setVariable(variable);
        return variableResult;
    }

    public ListVariableResult withCurrentVariable(ServerVariablesListVariable currentVariable) {
        this.currentVariable = currentVariable;
        return this;
    }

    public ListVariableResult withServerVariablesPlayer(ServerVariablesPlayer serverVariablesPlayer) {
        this.serverVariablesPlayer = serverVariablesPlayer;
        return this;
    }

    public StringVariableResult toStringVariableResult() {
        return new StringVariableResult(error, errorMessage, null, errorKey);
    }

    public ServerVariablesListVariable getCurrentVariable() {
        return currentVariable;
    }

    public ServerVariablesPlayer getServerVariablesPlayer() {
        return serverVariablesPlayer;
    }
}
