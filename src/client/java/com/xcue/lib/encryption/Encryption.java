package com.xcue.lib.encryption;

import org.jasypt.util.text.AES256TextEncryptor;

// Normally I would store an instance variable, but this is being written
// in unknown places so it needs to create a new instance every time.
public class Encryption {
    public static String decrypt(String string) {
        AES256TextEncryptor encryptor = new AES256TextEncryptor();
        encryptor.setPassword("poopy-stinky-poop");

        return encryptor.decrypt(string);
    }
}
