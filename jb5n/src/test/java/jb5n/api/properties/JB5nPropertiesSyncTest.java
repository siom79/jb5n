package jb5n.api.properties;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Locale;

import jb5n.api.JB5nException;
import jb5n.api.JB5nException.Reason;

import org.junit.Test;

public class JB5nPropertiesSyncTest {

	@Test
	public void testSync() {
        JB5nPropertiesSync.JB5nPropertiesSyncResult result = JB5nPropertiesSync.sync(MySyncMessageResource.class, new Locale("de_DE"), JB5nPropertiesSyncTest.class.getClassLoader());
		assertThat(result.getMissingResourceKeys(), hasItem("missingResource"));
		assertThat(result.getMissingResourceKeys(), hasItem("custom.key.notAvailable"));
	}

	private interface MessageResourceWithoutPropertiesFile {

	}

	@Test
	public void noPropertiesFileAvailable() {
		boolean exceptionThrown = false;
		try {
			JB5nPropertiesSync.sync(MessageResourceWithoutPropertiesFile.class, new Locale("en_GB"), JB5nPropertiesSyncTest.class.getClassLoader());
		} catch (JB5nException e) {
			exceptionThrown = true;
			assertThat(e.getReason(), is(Reason.MissingResource));
		}
		assertThat(exceptionThrown, is(true));
	}
}
