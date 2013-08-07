#jb5n#

jb5n is a Java library that allows you to access properties files via Java methods instead of the standard string-based access:

	MyMessageResource myMessageResource = JB5n.createInstance(MyMessageResource.class);
	String youHaveThreeRetries = myMessageResource.youHaveRetries(3); // "You have {0} retries."

##Motivation##

The Google Web Toolkit (GWT) comes with a mechanism called [Messages interface](http://www.gwtproject.org/doc/latest/DevGuideI18nMessages.html) that allows developers of GWT applications to access messages for internationalization via a Java interface instead of a properties string. This approach has some advantages compared to the standard properties file approach, as the access to the messages is done via typed methods and not via strings. In large projects it is simple to detect where a certain message is used, as the IDE lets you look up all invocations of the interface method. You can also easily check if for all methods used a corresponding translation exists. The other way round, you can also check which translations are no longer used. With string access to a properties file these tasks are very tedious.

##Features##

* **Simple to use**: Just create a Java interface and a corresponding properties file to get started.
* **Extensible**: Implementation of your own invocation handler allows you to retrieve the messages from any resource you like (e.g. database).
* **Inheritance**: In large projects you may want to swap out basic messages like "OK", "Cancel", etc. into a common super interface and inherit these messages into specific interfaces for each module or dialog.
* **Default messages**: If no message is found for a called message a default message can be defined via annotation. This way you can speed up development time as during development you only have to specify the default message. Further translations can be done later on.
* **Message format**: You can pass arguments to the methods, that are applied to the resource string via the standard [MessageFormat](http://docs.oracle.com/javase/6/docs/api/java/text/MessageFormat.html) mechanism.
* **Missing resource behaviour**: The behavior in case a resource string from the properties file is missing is configurable. You can either let jb5n throw an exception (recommended during development) or output the name of the method as translation (fail safe behavior).

Planned features (not yet available):

* **maven plugin**: A maven plugin that optionally checks during the build process that for each message method a corresponding key/value pair is available for all supported languages.

##Usage##

###Basics###

Just create an interface with some methods that return a String:

	public interface MyMessageResource {
		String ok();
		String cancel();
	}

Create a corresponding properties file in the same package as the interface that contains for each method name a message:

	ok=OK
	cancel=Cancel

Now retrieve a proxy for the interface and access the messages via methods:

	MyMessageResource myMessageResource = JB5n.createInstance(MyMessageResource.class);
	String ok = myMessageResource.ok();

###Default messages###

In order to speed up development time, you can add a default message for each method:

	@Message(defaultMessage = "OK")
	String ok();

###User specific key###

Per default, the method name is used as key in the properties file. To migrate existing projects you can define the key as annotation:

	@Message(key = "no.default.key")
	String noDefaultKey();

###User specific resource bundle###

Per default the resource bundle is derived from the class name. But you can define the resource bundle name via annotation for each interface separately:

	@MessageResource(resourceBundleName = "jb5n.api.MyResourceBundle")
	public interface MessageResourceWithOwnPropertiesFile {
		String ok();
	}

###Inheritance###

Message can be inherited from a common super interface:

	public interface MyMessageResource {
		String ok();
	}

	public interface MySpecificMessageResource extends MyMessageResource {
		String specificMessage();
	}

Now a call of ok() will retrieve the message from MyMessageResource.properties, whereas specificMessage() is retrieved from MySpecificMessageResource.properties. This way you can swap out commonly used messages.

If you want to use the same file for both interfaces, just use the resourceBundleName attribute of @MessageResource and let both interfaces point to the same resource.

###Message format###

You can pass arguments to the methods that are incorporated into the message using the standard Java [MessageFormat](http://docs.oracle.com/javase/6/docs/api/java/text/MessageFormat.html):

	public interface MyMessageResource {
		String youHaveNREtries(int numberOfRetries); // "You have {0} retries."
	}

###Extensible###

If your messages are not stored within properties files, you can implement your own mechanism to retrieve the messages. Just create a class that implements the interface JB5nInvocationHandler:

	public class MyDatabaseMessageResource implements JB5nInvocationHandler {
		...
	}

Define the InvocationHandler via the @MessageResource annotation:

	@MessageResource(invocationHandler=MyDatabaseMessageResource.class)
	private interface MyInvocationHandler {
		String ok();
	}

##Alternatives##

* [Compiler aware internationalization](http://blog.codecentric.de/en/2012/01/compiler-aware-internationalization-i18n-with-java-resourcebundle/): Blog entry about the concept.
* [Owner](https://github.com/lviggiano/owner/): Simple implementation of the concept.
* [C10N](https://github.com/rodionmoiseev/c10n): Java library that uses resource bundles and annotations to provide translations.

##Development##

* [Jenkins build server](https://siom79.ci.cloudbees.com/job/jb5n) [![Build Status](https://siom79.ci.cloudbees.com/job/jb5n/badge/icon)](https://siom79.ci.cloudbees.com/job/jb5n)