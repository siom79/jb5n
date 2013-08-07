package jb5n.api.properties;

import jb5n.api.Message;

public interface MySyncMessageResource {
    String missingResource();

    String resourceAvailable();

    @Message(key = "custom.key.notAvailable")
    String missingCustomKey();

    @Message(key = "custom.key.available")
    String customKeyAvailable();
}
