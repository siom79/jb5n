package jb5n.api;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Locale;

import jb5n.api.JB5nException.Reason;

import org.junit.Test;

public class JB5nTest {
	private static final Locale LOCALE_DE = new Locale("de");
	private static final Locale LOCALE_EN = new Locale("en");

	@Test
	public void proxyIsCreated() {
		MyMessageResource myMessageResource = JB5n.createInstance(MyMessageResource.class);
		assertThat(myMessageResource, notNullValue());
		String ok = myMessageResource.ok();
		assertThat(ok, is("OK"));
	}

	@Test(expected = JB5nException.class)
	public void reactToNullArgument() {
		JB5n.createInstance(null);
	}

	@MessageResource
	private class ClassWithMessageResourceAnnotation {

	}

	@Test(expected = JB5nException.class)
	public void reactionToClassWithMessageResourceAnnotation() {
		JB5n.createInstance(ClassWithMessageResourceAnnotation.class);
	}

	@Test
	public void loadPropertiesFile() {
		Locale.setDefault(LOCALE_EN);
		MyMessageResource instanceEn = JB5n.createInstance(MyMessageResource.class);
		String cancel = instanceEn.cancel();
		assertThat(cancel, is("Cancel"));
		Locale.setDefault(LOCALE_DE);
		MyMessageResource instanceDe = JB5n.createInstance(MyMessageResource.class);
		cancel = instanceDe.cancel();
		assertThat(cancel, is("Abbruch"));
	}

	@Test
	public void testMessageFormat() {
		MyMessageResource instance = JB5n.createInstance(MyMessageResource.class, LOCALE_EN);
		assertThat(instance.youHaveNREtries(5), is("You still have 5 retries."));
		instance = JB5n.createInstance(MyMessageResource.class, LOCALE_DE);
		assertThat(instance.youHaveNREtries(5), is("Du hast noch 5 Versuche."));
	}

	@Test
	public void noDefaultKey() {
		MyMessageResource instance = JB5n.createInstance(MyMessageResource.class, LOCALE_EN);
		assertThat(instance.noDefaultKey(), is("No default key."));
	}

	@MessageResource(resourceBundleName = "jb5n.api.MySpecificMessageResource")
	public interface MySpecificMessageResource extends MyMessageResource {
		String specificMessage();
	}

	@MessageResource(resourceBundleName = "jb5n.api.MyMessageResource")
	public interface MySpecificMessageResourceWithoutAnnotation extends MyMessageResource {
		String specificMessage();
	}

	@Test
	public void inheritedMessage() {
		MySpecificMessageResource instance = JB5n.createInstance(MySpecificMessageResource.class, LOCALE_EN);
		assertThat(instance.myMessageResource(), is("myMessageResource"));
		assertThat(instance.specificMessage(), is("Specific message from MySpecificMessageResource."));
		MySpecificMessageResourceWithoutAnnotation instance2 = JB5n.createInstance(MySpecificMessageResourceWithoutAnnotation.class, LOCALE_EN);
		assertThat(instance2.ok(), is("OK"));
		assertThat(instance2.specificMessage(), is("Specific message from MyMessageResource."));
		MyMessageResource instance3 = JB5n.createInstance(MyMessageResource.class);
		assertThat(instance3.ok(), is("OK"));
	}

	@Test
	public void missingResourceWithNoRaiseException() {
		MyMessageResource instance = JB5n.createInstance(MyMessageResource.class);
		assertThat(instance.missingResource(), is("???missingResource???"));
	}

	@Test(expected = JB5nException.class)
	public void missingResourceWithRaiseException() {
		JB5nConfiguration jb5nConfiguration = new JB5nConfiguration();
		jb5nConfiguration.setRaiseExceptionForMissingResource(true);
		JB5n.setConfiguration(jb5nConfiguration);
		MyMessageResource instance = JB5n.createInstance(MyMessageResource.class);
		instance.missingResource();
	}

	@Test
	public void messageResourceWithOwnResourceBundleName() {
		MessageResourceWithOwnPropertiesFile instance = JB5n.createInstance(MessageResourceWithOwnPropertiesFile.class);
		assertThat(instance.ok(), is("OK"));
	}

	@Test(expected = JB5nException.class)
	public void setConfigurationToNull() {
		JB5n.setConfiguration(null);
	}

	@MessageResource
	private interface NoPropertiesFileMessageResource {
		String ok();
	}

	@Test
	public void noPropertiesFileMessageResource() {
		boolean exceptionThrown = false;
		try {
			JB5nConfiguration jb5nConfiguration = new JB5nConfiguration();
			jb5nConfiguration.setRaiseExceptionForMissingResource(true);
			JB5n.setConfiguration(jb5nConfiguration);
			NoPropertiesFileMessageResource instance = JB5n.createInstance(NoPropertiesFileMessageResource.class);
			instance.ok();
		} catch (JB5nException e) {
			exceptionThrown = true;
			assertThat(e.getReason(), is(Reason.MissingResource));
		}
		assertThat(exceptionThrown, is(true));
	}

	@Test
	public void testCache() {
		JB5nConfiguration jb5nConfiguration = new JB5nConfiguration();
		jb5nConfiguration.setCacheMessageResources(true);
		JB5n.setConfiguration(jb5nConfiguration);
		int lastHashCode = 0;
		for (int i = 0; i < 10; i++) {
			MyMessageResource instance = JB5n.createInstance(MyMessageResource.class);
			int newHashCode = System.identityHashCode(instance);
			if (i > 0) {
				assertThat(newHashCode, is(lastHashCode));
			}
			lastHashCode = newHashCode;
		}
		jb5nConfiguration.setCacheMessageResources(false);
		MyMessageResource instance = JB5n.createInstance(MyMessageResource.class);
		int newHashCode = System.identityHashCode(instance);
		assertThat(newHashCode, not(is(lastHashCode)));
	}
	
	@Test
	public void wrongMethodSignature() {
		boolean exceptionThrown = false;
		try {
			MessageResourceWithWrongMethodSignature instance = JB5n.createInstance(MessageResourceWithWrongMethodSignature.class);
			instance.invalidMethodSignature();
		} catch (JB5nException e) {
			exceptionThrown = true;
			assertThat(e.getReason(), is(Reason.InvalidMethodSignature));
		}
		assertThat(exceptionThrown, is(true));
	}
	
	@MessageResource(invocationHandler=MyInvocationHandler.class)
	private interface MyInvocationHandlerAnnotation {
		String ok();
	}
	
	@Test
	public void testMyInvocationHandler() {
		MyInvocationHandlerAnnotation myInvocationHandler = JB5n.createInstance(MyInvocationHandlerAnnotation.class);
		myInvocationHandler.ok();
		assertThat(MyInvocationHandler.methodCalled, is(true));
	}
}
