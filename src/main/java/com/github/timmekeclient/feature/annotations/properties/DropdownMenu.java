package com.github.timmekeclient.feature.annotations.properties;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DropdownMenu {
    String label();

    boolean limitlessSelections() default false;

    int maximumSelections() default 1;

    String[] defaultValues() default {};

    String[] values();

    Class<? extends Annotation>[] requires() default {};
}
