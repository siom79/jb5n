package jb5n.api.properties;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import jb5n.api.JB5nException;
import jb5n.api.JB5nException.Reason;
import jb5n.api.Message;
import jb5n.internal.MessageResourceVerification;

public class JB5nPropertiesSync {

    public static class JB5nPropertiesSyncResult {
        private List<String> missingResourceKeys = new LinkedList<String>();

        public List<String> getMissingResourceKeys() {
            return missingResourceKeys;
        }
    }

    public static <T> JB5nPropertiesSyncResult sync(Class<T> clazz, Locale locale, ClassLoader classLoader) {
        JB5nPropertiesSyncResult result = new JB5nPropertiesSyncResult();
        MessageResourceVerification.verify(clazz, locale, classLoader);
        String resourceBundleName = JB5nPropertiesInvocationHandler.deriveResourceBundleName(clazz);
        ResourceBundle resourceBundle = null;
        try {
            resourceBundle = ResourceBundle.getBundle(resourceBundleName, locale, classLoader);
        } catch (MissingResourceException e) {
            throw new JB5nException(Reason.MissingResource, String.format("Resource bundle '%s' is missing: %s.", resourceBundleName, e.getMessage()), e);
        }
        List<String> missingResourceKeys = result.getMissingResourceKeys();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            Message messageAnnotationFromMethod = method.getAnnotation(Message.class);
            String resourceKey = JB5nPropertiesInvocationHandler.deriveResourceKey(methodName, messageAnnotationFromMethod);
            try {
                resourceBundle.getString(resourceKey);
            } catch (MissingResourceException e) {
                missingResourceKeys.add(resourceKey);
            }
        }
        return result;
    }
}
