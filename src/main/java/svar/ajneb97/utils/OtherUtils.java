package svar.ajneb97.utils;

import svar.ajneb97.ServerVariables;
import svar.ajneb97.model.internal.ValueFromArgumentResult;

public class OtherUtils {

    public static boolean isNew() {
        ServerVersion serverVersion = ServerVariables.serverVersion;
        return serverVersion.serverVersionGreaterEqualThan(serverVersion, ServerVersion.v1_16_R1);
    }

    public static ValueFromArgumentResult getValueFromArgument(String[] args, int startArg) {
        String value = args[startArg];
        int valueExtraArgs = 0;
        if (value.startsWith("\"")) {
            StringBuilder newValueWithSpaces = new StringBuilder(value); // "value with spaces"
            for (int i = startArg + 1; i < args.length; i++) {
                String arg = args[i];
                newValueWithSpaces.append(" ").append(arg);
                valueExtraArgs++;
                if (arg.endsWith("\"")) {
                    break;
                }
            }

            if (!newValueWithSpaces.toString().startsWith("\"") || !newValueWithSpaces.toString().endsWith("\"")) {
                return null;
            }

            value = newValueWithSpaces.toString().replace("\"", "");
        }

        return new ValueFromArgumentResult(value, valueExtraArgs);
    }
}
