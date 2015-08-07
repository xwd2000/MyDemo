package com.example.annotation.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.mydemos.AppApplication;

@Target(ElementType.FIELD)   //用于构造方法
@Retention(RetentionPolicy.RUNTIME) //在运行时加载到Annotation到JVM中
public @interface DbColumn {
	String isColumn() default AppApplication.PUSH_HOST;
}
