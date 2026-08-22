package svar.ajneb97.utils;

import svar.ajneb97.ServerVariables;
import svar.ajneb97.model.internal.ValueFromArgumentResult;

public class OtherUtils {

    public static boolean isNew() {
        ServerVersion serverVersion = ServerVariables.serverVersion;
        if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_16_R1)){
            return true;
        }
        return false;
    }

    public static String getFileExtension(String filePath) {
        int lastIndex = filePath.lastIndexOf(".");
        if (lastIndex > 0 && lastIndex < filePath.length() - 1) {
            return filePath.substring(lastIndex+1);
        } else {
            return "invalid";
        }
    }

    public static ValueFromArgumentResult getValueFromArgument(String[] args, int startArg){
        String value = args[startArg];
        int valueExtraArgs = 0;

        if (value.startsWith("\"")) {
            if (value.endsWith("\"") && value.length() > 1) {
                value = value.substring(1, value.length() - 1);
                return new ValueFromArgumentResult(value, valueExtraArgs);
            }

            StringBuilder newValueWithSpaces = new StringBuilder(value);
            for (int i = startArg + 1; i < args.length; i++) {
                String arg = args[i];

                newValueWithSpaces.append(" ").append(arg);
                valueExtraArgs++;

                if (arg.endsWith("\"")) {
                    break;
                }
            }

            if (!newValueWithSpaces.toString().endsWith("\"")) {
                return null;
            }

            value = newValueWithSpaces.substring(1, newValueWithSpaces.length() - 1);
        }

        return new ValueFromArgumentResult(value, valueExtraArgs);
    }
}
