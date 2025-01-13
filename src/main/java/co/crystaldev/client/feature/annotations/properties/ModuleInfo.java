package co.crystaldev.client.feature.annotations.properties;

import co.crystaldev.client.feature.base.Category;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleInfo {
    String name();

    String[] nameAliases() default {};

    String description();

    Category category();

    boolean isNew() default false;
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\annotations\properties\ModuleInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */