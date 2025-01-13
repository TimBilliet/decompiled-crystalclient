package com.github.lunatrius.core.exceptions;

import net.minecraft.util.StatCollector;

public class LocalizedException extends Exception {
    public LocalizedException(String format) {
        super(StatCollector.translateToLocal(format));
    }

    public LocalizedException(String format, Object... arguments) {
        super(StatCollector.translateToLocalFormatted(format, arguments));
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\core\exceptions\LocalizedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */