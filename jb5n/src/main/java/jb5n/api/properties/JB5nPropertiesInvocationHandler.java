package jb5n.api.properties;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import jb5n.api.JB5n;
import jb5n.api.JB5nException;
import jb5n.api.JB5nException.Reason;
import jb5n.api.JB5nInvocationHandler;
import jb5n.api.Message;
import jb5n.api.MessageResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JB5nPropertiesInvocationHandler implements JB5nInvocationHandler {
	private static final Logger logger = LoggerFactory.getLogger(JB5nPropertiesInvocationHandler.class);
	private Locale locale;
	private ClassLoader classLoader;

	public Object invoke(Object proxy, Method method, Object[] methodArgs) throws Throwable {
		String returnValue = "";
		String methodName = method.getName();
		Class<?> declaringClass = method.getDeclaringClass();
		logInvoke(proxy, methodName);
		String resourceBundleName = deriveResourceBundleName(declaringClass);
		Message messageAnnotationFromMethod = getMessageAnnotationFromMethod(method, methodArgs);
		ResourceBundle resourceBundle = null;
		try {
			resourceBundle = ResourceBundle.getBundle(resourceBundleName, locale, classLoader);
		} catch (MissingResourceException e) {
			returnValue = handleMissingResourceException(methodName, messageAnnotationFromMethod,
					String.format("Missing resource bundle '%s' for locale '%s'.", resourceBundleName, locale));
		}
		returnValue = retrieveMessageFromResourceBundle(methodArgs, returnValue, methodName, resourceBundleName, messageAnnotationFromMethod, locale, resourceBundle);
		return returnValue;
	}

	private String retrieveMessageFromResourceBundle(Object[] methodArgs, String returnValue, String methodName, String resourceBundleName, Message messageAnnotationFromMethod,
			Locale defaultLocale, ResourceBundle resourceBundle) {
		if (resourceBundle != null) {
			String resourceKey = deriveResourceKey(methodName, messageAnnotationFromMethod);
			try {
				String message = resourceBundle.getString(resourceKey);
				if (methodArgs != null && methodArgs.length > 0) {
					MessageFormat messageFormat = new MessageFormat(message);
					returnValue = messageFormat.format(methodArgs);
				} else {
					returnValue = message;
				}
			} catch (MissingResourceException e) {
				returnValue = handleMissingResourceException(methodName, messageAnnotationFromMethod,
						String.format("Missing key '%s' in resource bundle '%s' for locale '%s'.", resourceKey, resourceBundleName, defaultLocale));
			}
		}
		return returnValue;
	}

	protected static String deriveResourceKey(String methodName, Message messageAnnotationFromMethod) {
		String resourceKey = methodName;
		if (messageAnnotationFromMethod != null) {
			String userDefinedKey = messageAnnotationFromMethod.key();
			if (userDefinedKey != null && userDefinedKey.trim().length() > 0) {
				resourceKey = userDefinedKey;
			}
		}
		return resourceKey;
	}

	private void logInvoke(Object proxy, String methodName) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Calling %s() on proxy %s for %s.", methodName, proxy.getClass().getSimpleName(), proxy.getClass().getSuperclass().getSimpleName()));
		}
	}

	private String handleMissingResourceException(String methodName, Message messageAnnotationFromMethod, String message) {
		String returnValue;
		if (messageAnnotationFromMethod != null) {
			returnValue = messageAnnotationFromMethod.defaultMessage();
		} else {
			boolean raiseExceptionForMissingResource = JB5n.getConfiguration().isRaiseExceptionForMissingResource();
			if (raiseExceptionForMissingResource) {
				throw new JB5nException(Reason.MissingResource, message);
			}
			returnValue = createDefaultErrorReturnValue(methodName);
		}
		return returnValue;
	}

	protected static String deriveResourceBundleName(Class<?> declaringClass) {
		MessageResource annotation = declaringClass.getAnnotation(MessageResource.class);
		if (annotation != null) {
			String resourceBundleName = annotation.resourceBundleName();
			if (resourceBundleName != null && resourceBundleName.trim().length() > 0) {
				return resourceBundleName;
			}
		}
		return declaringClass.getName();
	}

	private String createDefaultErrorReturnValue(String methodName) {
		return "???" + methodName + "???";
	}

	private Message getMessageAnnotationFromMethod(Method method, Object[] args) throws NoSuchMethodException {
		Method methodOnClass = getCorrespondingMethod(method, args);
		return methodOnClass.getAnnotation(Message.class);
	}

	private Method getCorrespondingMethod(Method method, Object[] args) throws NoSuchMethodException {
		Class<?> declaringClass = method.getDeclaringClass();
		try {
			return declaringClass.getMethod(method.getName(), method.getParameterTypes());
		} catch (Exception e) {
			throw new JB5nException(Reason.InternalError, String.format("Unable to find matching method for type %s with name %s and args %s.",
					declaringClass.getSimpleName(), method.getName(), method.getParameterTypes()));
		}
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public void setClassLoader(ClassLoader loader) {
		this.classLoader = loader;
	}
}
