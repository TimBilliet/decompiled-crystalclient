package com.github.timmekeclient.feature.annotations.properties;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Selector {
    String label();

    String[] values();

    Class<? extends Annotation>[] requires() default {};
}
