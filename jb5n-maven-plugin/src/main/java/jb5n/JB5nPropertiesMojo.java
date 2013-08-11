package jb5n;

import javassist.ClassPool;
import javassist.CtClass;
import jb5n.api.MessageResource;
import jb5n.api.properties.JB5nPropertiesSync;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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

    /**
     * @parameter alias="breakBuild"
     */
    private String breakBuild;

    public void execute() throws MojoExecutionException {
        File artifactFile = getArtifactFile();
        boolean breakBuildBoolean = getBreakBuild();
        List<String> messageResources = getMessageResources();
        if (locales != null) {
            addMessageResourcesFromJarFile(artifactFile, messageResources);
            processMessageResources(artifactFile, breakBuildBoolean, messageResources);
        } else {
            logNothingToDo();
        }
    }

    private void processMessageResources(File artifactFile, boolean breakBuildBoolean, List<String> messageResources) throws MojoExecutionException {
        URI artifactUri = artifactFile.toURI();
        for (String messageInterface : messageResources) {
            getLog().info(String.format("Processing message resource '%s' for file '%s'.", messageInterface, artifactUri));
            JB5nPropertiesMojoClassLoader classLoader = new JB5nPropertiesMojoClassLoader(new URL[0], getParentClassLoader());
            try {
                classLoader.addUrl(artifactUri.toURL());
                Class<?> messageInterfaceClass = classLoader.loadClass(messageInterface);
                for (String localeString : locales) {
                    Locale locale = new Locale(localeString);
                    JB5nPropertiesSync.JB5nPropertiesSyncResult syncResult = JB5nPropertiesSync.sync(messageInterfaceClass, locale, classLoader);
                    for (String missingResourceKey : syncResult.getMissingResourceKeys()) {
                        String message = String.format("Missing resource key '%s' for message resource '%s'.", missingResourceKey, messageInterface);
                        if (breakBuildBoolean) {
                            throw new MojoExecutionException(message);
                        } else {
                            getLog().warn(message);
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                throw new MojoExecutionException(String.format("Failed to load message-interface class %s from artifact file %s.", messageInterface, artifactFile.getAbsoluteFile()));
            } catch (MalformedURLException e) {
                throw new MojoExecutionException(String.format("Failed to convert artifact file to URL: %s.", e.getMessage()));
            }
        }
    }

    private void addMessageResourcesFromJarFile(File artifactFile, List<String> messageResources) throws MojoExecutionException {
        try {
            JarFile jarFile = new JarFile(artifactFile);
            Enumeration<JarEntry> entries = jarFile.entries();
            ClassPool classPool = ClassPool.getDefault();
            while(entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String jarEntryName = jarEntry.getName();
                if(jarEntryName != null && jarEntryName.endsWith(".class")) {
                    getLog().debug(String.format("Processing jar file entry '%s'.", jarEntryName));
                    try {
                        CtClass ctClass = classPool.makeClass(jarFile.getInputStream(jarEntry));
                        Object annotation = ctClass.getAnnotation(MessageResource.class);
                        if(annotation != null) {
                            jarEntryName = jarEntryName.replace("/", ".");
                            jarEntryName = jarEntryName.replaceAll("\\.class$", "");
                            messageResources.add(jarEntryName);
                        }
                    } catch (Throwable e) {
                        getLog().warn(String.format("Processing bytecode of class file '%s' failed: %s.", jarEntryName, e.getMessage()));
                    }
                }
            }
        } catch (IOException e) {
            throw new MojoExecutionException(String.format("The file '%s' is no jar file. Please apply this plugin only for artifacts with packaging types that can be access as jar file."), e);
        }
    }

    private URLClassLoader getParentClassLoader() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        if(classLoader instanceof URLClassLoader) {
            return (URLClassLoader) classLoader;
        }
        return null;
    }

    private List<String> getMessageResources() {
        if(messageIntefaces == null) {
            return new LinkedList<String>();
        }
        return new LinkedList<String>(Arrays.asList(messageIntefaces));
    }

    private File getArtifactFile() throws MojoExecutionException {
        File file = mavenProject.getArtifact().getFile();
        if (file == null) {
            throw new MojoExecutionException(String.format("Could not access file of this artifact. Please call this plugin in a lifecycle phase where this artifact's file is accessible (i.e. after package)."));
        }
        return file;
    }

    private boolean getBreakBuild() {
        boolean breakBuildBoolean = false;
        if (breakBuild != null) {
            breakBuildBoolean = Boolean.valueOf(breakBuild);
        }
        return breakBuildBoolean;
    }

    private void logNothingToDo() {
        if (locales == null) {
            getLog().info("No locales configured. Nothing to do.");
        }
    }
}
