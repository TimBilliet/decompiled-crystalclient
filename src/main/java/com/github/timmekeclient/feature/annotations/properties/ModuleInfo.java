package com.github.timmekeclient.feature.annotations.properties;

import com.github.timmekeclient.feature.base.Category;

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
