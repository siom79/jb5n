package jb5n;

import jb5n.api.properties.JB5nPropertiesSync;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

/**
 * Goal which verifies the properties file.
 *
 * @goal verify
 * @phase verify
 */
public class JB5nPropertiesMojo extends AbstractMojo {

    /**
     * @parameter default-value="${project}"
     */
    private MavenProject mavenProject;

    /**
     * @parameter alias="message-interfaces"
     */
    private String[] messageIntefaces;

    /**
     * @parameter alias="locales"
     */
    private String[] locales;

    public void execute() throws MojoExecutionException {
        File file = mavenProject.getArtifact().getFile();
        if (file == null) {
            throw new MojoExecutionException(String.format("Could not access file of this artifact. Please call this plugin in a lifecycle phase where the file is accessible (i.e. after package)."));
        }
        URI fileUri = file.toURI();
        if (messageIntefaces != null && locales != null) {
            for (String messageInterface : messageIntefaces) {
                getLog().info(String.format("Processing %s for file %s.", messageInterface, file.toURI()));
                JB5nPropertiesMojoClassLoader classLoader = new JB5nPropertiesMojoClassLoader(new URL[0]);
                try {
                    classLoader.addUrl(fileUri.toURL());
                    Class<?> messageInterfaceClass = classLoader.loadClass(messageInterface);
                    for (String localeString : locales) {
                        Locale locale = new Locale(localeString);
                        List<String> syncs = JB5nPropertiesSync.sync(messageInterfaceClass, locale, classLoader);
                        for(String sync : syncs) {
                            getLog().info(sync);
                        }
                    }
                } catch (ClassNotFoundException e) {
                    throw new MojoExecutionException(String.format("Failed to load message-interface class %s from artifact file %s.", messageInterface, file.getAbsoluteFile()));
                } catch (MalformedURLException e) {
                    throw new MojoExecutionException(String.format("Failed to convert artifact file to URL: %s.", e.getMessage()));
                }
            }
        } else {
            if(messageIntefaces == null) {
                getLog().info("No message-interfaces configured. Nothing to do.");
            }
            if(locales == null) {
                getLog().info("No locales configured. Nothing to do.");
            }
        }
    }
}
