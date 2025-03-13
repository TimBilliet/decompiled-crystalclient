package com.github.timmekeclient.feature.annotations.properties;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Colour {
    String label();

    boolean isTextRender() default false;

    Class<? extends Annotation>[] requires() default {};
}
