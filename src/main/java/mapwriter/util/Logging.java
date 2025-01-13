package mapwriter.util;

import co.crystaldev.client.Reference;

public class Logging {
    public static void logInfo(String s, Object... args) {
        Reference.LOGGER.info(String.format(s, args));
    }

    public static void logWarning(String s, Object... args) {
        Reference.LOGGER.warn(String.format(s, args));
    }

    public static void logError(String s, Object... args) {
        Reference.LOGGER.error(String.format(s, args));
    }

    public static void debug(String s, Object... args) {
        Reference.LOGGER.debug(String.format(s, args));
    }

    public static void log(String s, Object... args) {
        logInfo(String.format(s, args), new Object[0]);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwrite\\util\Logging.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */