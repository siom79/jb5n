package jb5n.api;

import java.lang.reflect.InvocationHandler;
import java.util.Locale;

public interface JB5nInvocationHandler extends InvocationHandler {

    /**
     * Sets the locale the MessageResource is requested for.
     *
     * @param locale the Locale
     */
    void setLocale(Locale locale);

    /**
     * Sets the ClassLoader that can be used to load e.g. a resource bundle.
     *
     * @param loader the ClassLoader
     */
    void setClassLoader(ClassLoader loader);
}
