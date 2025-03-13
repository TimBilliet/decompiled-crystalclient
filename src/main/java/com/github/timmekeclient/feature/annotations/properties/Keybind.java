package com.github.timmekeclient.feature.annotations.properties;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Keybind {
    String label();

    Class<? extends Annotation>[] requires() default {};
}
