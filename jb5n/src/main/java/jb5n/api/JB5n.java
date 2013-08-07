package jb5n.api;

import java.lang.reflect.Proxy;
import java.util.Locale;

import jb5n.api.JB5nException.Reason;
import jb5n.api.properties.JB5nPropertiesInvocationHandler;
import jb5n.internal.MessageResourceVerification;
import jb5n.internal.ProxyCache;
import jb5n.internal.ProxyCache.ProxyKey;

public class JB5n {
	private static JB5nConfiguration configuration = new JB5nConfiguration();
	private static ProxyCache proxyCache = new ProxyCache();

	/**
	 * Creates an instance of the given class that represents a MessageResource.
	 * 
	 * @param clazz
	 *            an interface that is annotated with @see MessageResource.
	 * @param locale
	 *            the Locale used for retrieving the message
	 * @param loader
	 *            the ClassLoader used to load e.g. the ResourceBundle
	 * @return a proxy for the given class that represents the underlying
	 *         MessageResource
	 */
	@SuppressWarnings("unchecked")
	public static <T> T createInstance(Class<T> clazz, Locale locale, ClassLoader loader) {
		MessageResourceVerification.verify(clazz, locale, loader);
		if (configuration.isCacheMessageResources()) {
			ProxyKey key = new ProxyKey(clazz, locale, loader);
			Object proxyFromCache = proxyCache.get(key);
			if (proxyFromCache != null) {
				return (T) proxyFromCache;
			}
		}
		Class<? extends JB5nInvocationHandler> invocationHandlerClass = determineInvocationHandler(clazz);
		JB5nInvocationHandler invocationHandler = createInstanceOfInvocationHandler(invocationHandlerClass);
		invocationHandler.setLocale(locale);
		invocationHandler.setClassLoader(loader);
		T proxy = createProxy(clazz, invocationHandler);
		if (configuration.isCacheMessageResources()) {
			ProxyKey key = new ProxyKey(clazz, locale, loader);
			proxyCache.put(key, proxy);
		}
		return proxy;
	}

	/**
	 * Creates an instance of the given class that represents a MessageResource
	 * using the default locale and the ClassLoader of the given interface.
	 * 
	 * @param clazz
	 *            an interface that is annotated with @see MessageResource.
	 * @param locale
	 *            the Locale used for retrieving the message
	 * @return a proxy for the given class that represents the underlying
	 *         MessageResource
	 */
	public static <T> T createInstance(Class<T> clazz, Locale locale) {
		return createInstance(clazz, locale, clazz.getClassLoader());
	}

	/**
	 * Creates an instance of the given class that represents a MessageResource
	 * using the default locale and the ClassLoader of the given interface.
	 * 
	 * @param clazz
	 *            an interface that is annotated with @see MessageResource.
	 * @return a proxy for the given class that represents the underlying
	 *         MessageResource
	 */
	public static <T> T createInstance(Class<T> clazz) {
		ClassLoader classLoader = JB5n.class.getClassLoader();
		if (clazz != null) {
			classLoader = clazz.getClassLoader();
		}
		return createInstance(clazz, Locale.getDefault(), classLoader);
	}

	@SuppressWarnings("unchecked")
	private static <T> T createProxy(Class<T> clazz, JB5nInvocationHandler invocationHandler) {
		try {
			Object proxyObject = Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[] { clazz }, invocationHandler);
			return (T) proxyObject;
		} catch (IllegalArgumentException e) {
			throw new JB5nException(Reason.InvalidArgument, String.format("The class %s does not adhere to the restrictions of a proxy class: %s",
					clazz.getClass().getSimpleName(), e.getMessage()), e);
		}
	}

	private static JB5nInvocationHandler createInstanceOfInvocationHandler(Class<? extends JB5nInvocationHandler> invocationHandlerClass) {
		JB5nInvocationHandler invocationHandler;
		try {
			invocationHandler = invocationHandlerClass.newInstance();
		} catch (Exception e) {
			throw new JB5nException(Reason.InvalidArgument, String.format("Creating an instance of %s failed: %s", invocationHandlerClass.getSimpleName(), e.getMessage()), e);
		}
		return invocationHandler;
	}

	private static <T> Class<? extends JB5nInvocationHandler> determineInvocationHandler(Class<T> clazz) {
		MessageResource messageResourceAnnotation = clazz.getAnnotation(MessageResource.class);
		Class<? extends JB5nInvocationHandler> invocationHandlerClass = JB5nPropertiesInvocationHandler.class;
		if (messageResourceAnnotation != null) {
			invocationHandlerClass = messageResourceAnnotation.invocationHandler();
			if (invocationHandlerClass == null) {
				invocationHandlerClass = JB5nPropertiesInvocationHandler.class;
			}
		}
		return invocationHandlerClass;
	}

	/**
	 * Sets the global configuration.
	 * 
	 * @param jb5nConfiguration
	 *            the configuration
	 */
	public static void setConfiguration(JB5nConfiguration jb5nConfiguration) {
		if (jb5nConfiguration == null) {
			throw new JB5nException(Reason.InvalidArgument, "Do not pass null as configuration.");
		}
		configuration = jb5nConfiguration;
	}

	/**
	 * Returns the global configuration.
	 * 
	 * @return the configuration
	 */
	public static JB5nConfiguration getConfiguration() {
		return configuration;
	}
}
