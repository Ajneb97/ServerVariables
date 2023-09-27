package svar.ajneb97.utils;

public class OtherUtils {

    public static String getFileExtension(String filePath) {
        int lastIndex = filePath.lastIndexOf(".");
        if (lastIndex > 0 && lastIndex < filePath.length() - 1) {
            return filePath.substring(lastIndex+1);
        } else {
            return "invalid";
        }
    }
}
