package svar.ajneb97.utils;

public class MathUtils {

    public static double getDoubleSum(String value1,String value2,boolean add){
        double numericValue = Double.parseDouble(value1);
        return add ? Double.parseDouble(value2)+numericValue : Double.parseDouble(value2)-numericValue;
    }
}
