package jb5n.api;

public interface MyMessageResource {

	@Message(defaultMessage = "OK")
	String ok();

	String cancel();

	String youHaveNREtries(int numberOfRetries);

	@Message(key = "no.default.key")
	String noDefaultKey();

	String missingResource();
	
	String myMessageResource();
}
