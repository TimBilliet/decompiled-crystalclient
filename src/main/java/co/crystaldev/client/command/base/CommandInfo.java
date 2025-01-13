package co.crystaldev.client.command.base;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo {
    String name();

    String[] aliases() default {};

    String description();

    String[] usage() default {};

    int requiredArguments() default -1;

    int minimumArguments() default -1;

    int maximumArguments() default -1;
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\command\base\CommandInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */