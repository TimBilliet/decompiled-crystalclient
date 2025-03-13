package com.github.timmekeclient.feature.annotations.properties;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface PageBreak {
    String label() default "";

    int color() default 10395808;

    Class<? extends Annotation>[] requires() default {};
}
