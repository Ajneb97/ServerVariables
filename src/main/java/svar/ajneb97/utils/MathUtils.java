package svar.ajneb97.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtils {

    public static double getDoubleSum(String value1,String value2,boolean add){
        double numericValue = Double.parseDouble(value1);
        double newValue = add ? Double.parseDouble(value2)+numericValue : Double.parseDouble(value2)-numericValue;

        return BigDecimal.valueOf(newValue).setScale(3, RoundingMode.HALF_UP).doubleValue();
    }
}
