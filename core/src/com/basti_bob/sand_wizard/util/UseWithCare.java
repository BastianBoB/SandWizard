package com.basti_bob.sand_wizard.util;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UseWithCare {
    String value() default "This method should be used with care.";
}