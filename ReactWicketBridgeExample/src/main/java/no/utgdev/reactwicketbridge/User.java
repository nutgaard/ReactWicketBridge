package no.utgdev.reactwicketbridge;

import java.io.Serializable;

public class User implements Serializable {
    public String username;
    public String text;

    public User(String username, String text) {
        this.username = username;
        this.text = text;
    }

    public User() {
    }
}
