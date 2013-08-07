package jb5n.api;

import java.lang.reflect.Method;
import java.util.Locale;

import org.apache.log4j.Logger;

public class MyInvocationHandler implements JB5nInvocationHandler {
	private static final Logger logger = Logger.getLogger(MyInvocationHandler.class);
	public static boolean methodCalled = false;

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Calling method %s on proxy %s.", method.getName(), proxy.getClass()
					.getSimpleName()));
		}
		methodCalled = true;
		return method.getName();
	}

	public void setLocale(Locale locale) {
		
	}

	public void setClassLoader(ClassLoader loader) {
		
	}

}
