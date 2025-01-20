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
