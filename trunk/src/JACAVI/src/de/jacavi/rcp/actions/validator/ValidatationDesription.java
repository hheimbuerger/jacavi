package de.jacavi.rcp.actions.validator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;



@Retention(RetentionPolicy.RUNTIME)
public @interface ValidatationDesription {
    String value();
}
