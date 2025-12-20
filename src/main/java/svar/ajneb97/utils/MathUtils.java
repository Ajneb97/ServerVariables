package svar.ajneb97.utils;

public class MathUtils {

    public static double getDoubleSum(String value1, String value2, boolean add) {
        double numericValue = Double.parseDouble(value1);
        return add ? Double.parseDouble(value2) + numericValue : Double.parseDouble(value2) - numericValue;
    }

    // org.apache.commons.lang3.math.NumberUtils
    public static boolean isParsable(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        } else if (str.charAt(str.length() - 1) == '.') {
            return false;
        } else if (str.charAt(0) == '-') {
            return str.length() != 1 && withDecimalsParsing(str, 1);
        } else {
            return withDecimalsParsing(str, 0);
        }
    }

    // org.apache.commons.lang3.math.NumberUtils
    private static boolean withDecimalsParsing(String str, int beginIdx) {
        int decimalPoints = 0;

        for (int i = beginIdx; i < str.length(); ++i) {
            boolean isDecimalPoint = str.charAt(i) == '.';
            if (isDecimalPoint) {
                ++decimalPoints;
            }

            if (decimalPoints > 1) {
                return false;
            }

            if (!isDecimalPoint && !Character.isDigit(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }
}
