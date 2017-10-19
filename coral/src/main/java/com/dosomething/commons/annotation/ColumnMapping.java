package com.dosomething.commons.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({FIELD})
@Retention(RUNTIME)
public @interface ColumnMapping {
	
	// 資料庫欄位名稱
	String columnName() default "";

	// java bean的欄位名稱
	String fieldName() default "";
}
