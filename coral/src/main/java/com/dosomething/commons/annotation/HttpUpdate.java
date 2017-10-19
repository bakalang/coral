package com.dosomething.commons.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.METHOD;

/**
 * 是否可以透過http的方式來更改值
 */
@Target({FIELD, METHOD})
@Retention(RUNTIME)
public @interface HttpUpdate {

}
