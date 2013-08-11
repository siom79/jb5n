package jb5n.client;

import jb5n.api.Message;
import jb5n.api.MessageResource;

@MessageResource
public interface MyMessageWithAnnotation {

    @Message(key = "keys.cancel")
    String cancel();
}
