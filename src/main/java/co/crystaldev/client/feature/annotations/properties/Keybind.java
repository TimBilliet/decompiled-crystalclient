package co.crystaldev.client.feature.annotations.properties;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Keybind {
    String label();

    Class<? extends Annotation>[] requires() default {};
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\annotations\properties\Keybind.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */