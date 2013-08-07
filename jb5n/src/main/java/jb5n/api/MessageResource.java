package jb5n.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jb5n.api.properties.JB5nPropertiesInvocationHandler;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE })
public @interface MessageResource {
	Class<? extends JB5nInvocationHandler> invocationHandler() default JB5nPropertiesInvocationHandler.class;

	String resourceBundleName() default "";
}
