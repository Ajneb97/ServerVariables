package svar.ajneb97.model.internal;

public class ValueFromArgumentResult {

    private final String finalValue;
    private final int extraArgs;

    public ValueFromArgumentResult(String finalValue, int extraArgs) {
        this.finalValue = finalValue;
        this.extraArgs = extraArgs;
    }

    public String getFinalValue() {
        return finalValue;
    }

    public int getExtraArgs() {
        return extraArgs;
    }
}
