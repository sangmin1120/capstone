package smu.capstone.common.util;

import java.security.SecureRandom;

public class CertificationKeyGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final String BASIC_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String STRONG_CHARACTERS = BASIC_CHARACTERS + "!@#$%^&*()";

    public static String generateBasicKey() {
        return generateKey(BASIC_CHARACTERS, 6);
    }

    public static String generateStrongKey() {
        return generateKey(STRONG_CHARACTERS, 10);
    }

    private static String generateKey(String characterPool, int length) {
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(characterPool.length());
            key.append(characterPool.charAt(index));
        }
        return key.toString();
    }
}
