package svar.ajneb97.model.structure;

public class Limitations {
    private double minValue;
    private double maxValue;
    private int maxCharacters;

    public Limitations() {
        this.minValue = -Integer.MAX_VALUE;
        this.maxValue = Integer.MAX_VALUE;
        this.maxCharacters = Integer.MAX_VALUE;
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
}
