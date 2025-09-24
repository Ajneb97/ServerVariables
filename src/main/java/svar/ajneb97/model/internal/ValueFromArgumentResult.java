package svar.ajneb97.model.internal;

public class ValueFromArgumentResult {
    private String finalValue;
    private int extraArgs;

    public ValueFromArgumentResult(String finalValue, int extraArgs) {
        this.finalValue = finalValue;
        this.extraArgs = extraArgs;
    }

    public String getFinalValue() {
        return finalValue;
    }

    public void setFinalValue(String finalValue) {
        this.finalValue = finalValue;
    }

    public int getExtraArgs() {
        return extraArgs;
    }

    public void setExtraArgs(int extraArgs) {
        this.extraArgs = extraArgs;
    }
}
