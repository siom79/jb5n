package jb5n;

import java.net.URL;
import java.net.URLClassLoader;

public class JB5nPropertiesMojoClassLoader extends URLClassLoader {

    public JB5nPropertiesMojoClassLoader(URL[] urls) {
        super(urls);
    }

    public void addUrl(URL url) {
        super.addURL(url);
    }
}
