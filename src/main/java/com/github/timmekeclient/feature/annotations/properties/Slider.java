package com.github.timmekeclient.feature.annotations.properties;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Slider {
    String label();

    String placeholder() default "{value}";

    double minimum();

    double maximum();

    double standard();

    boolean integers() default false;

    Class<? extends Annotation>[] requires() default {};
}
