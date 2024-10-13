package com.xcue.lib.encryption;

import org.jasypt.util.text.AES256TextEncryptor;

public class Encryption {
    public static AES256TextEncryptor encryptor;
    private static final String KEY = "poopy-stinky-poop";

    static {
        encryptor = new AES256TextEncryptor();
        encryptor.setPassword(KEY);
    }

    public static String decrypt(String string) {
        return encryptor.decrypt(string);
    }
}
