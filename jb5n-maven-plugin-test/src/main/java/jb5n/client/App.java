package jb5n.client;

import jb5n.api.JB5n;

public class App {

    public static void main(String[] args) {
        MyMessages messages = JB5n.createInstance(MyMessages.class);
        messages.ok();
    }
}
