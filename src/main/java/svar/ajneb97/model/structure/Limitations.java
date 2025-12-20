package svar.ajneb97.model.structure;

public class Limitations {

    private double minValue;
    private double maxValue;
    private int maxCharacters;
    private int maxDecimals;
    private boolean manageOutOfRange;

    public Limitations() {
        this.minValue = -Double.MAX_VALUE;
        this.maxValue = Double.MAX_VALUE;
        this.maxCharacters = Integer.MAX_VALUE;
        this.maxDecimals = 5;
        this.manageOutOfRange = false;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public int getMaxCharacters() {
        return maxCharacters;
    }

    public void setMaxCharacters(int maxCharacters) {
        this.maxCharacters = maxCharacters;
    }

    public int getMaxDecimals() {
        return maxDecimals;
    }

    public void setMaxDecimals(int maxDecimals) {
        this.maxDecimals = maxDecimals;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isManageOutOfRange() {
        return manageOutOfRange;
    }

    public void setManageOutOfRange(boolean manageOutOfRange) {
        this.manageOutOfRange = manageOutOfRange;
    }
}
