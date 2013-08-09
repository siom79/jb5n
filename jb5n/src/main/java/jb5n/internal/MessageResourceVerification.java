package jb5n.internal;

import java.lang.reflect.Method;
import java.util.Locale;

import jb5n.api.JB5nException;
import jb5n.api.JB5nException.Reason;

public class MessageResourceVerification {

    public static <T> void verify(Class<T> clazz, Locale locale, ClassLoader classLoder) {
        if (clazz == null) {
            throw new JB5nException(Reason.InvalidArgument, "Argument clazz should not be null.");
        }
        if (!clazz.isInterface()) {
            throw new JB5nException(Reason.InvalidArgument, String.format("The class %s is no interface.", clazz.getSimpleName()));
        }
        verifyMethodSignatures(clazz);
        if (locale == null) {
            throw new JB5nException(Reason.InvalidArgument, "Argument locale should not be null.");
        }
        if (classLoder == null) {
            throw new JB5nException(Reason.InvalidArgument, "Argument classLoder should not be null.");
        }
    }

    private static <T> void verifyMethodSignatures(Class<T> clazz) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            Class<?> returnType = method.getReturnType();
            if (!String.class.isAssignableFrom(returnType)) {
                throw new JB5nException(Reason.InvalidMethodSignature, String.format("The method '%s' of class '%s' does not return a value of type String.", method.getName(), clazz.getName()));
            }
        }
    }
}
