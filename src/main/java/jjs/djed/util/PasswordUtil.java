package jjs.djed.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.nio.CharBuffer;

public class PasswordUtil {
    private static final BCryptPasswordEncoder BCRYPT = new BCryptPasswordEncoder(12);

    public static String hash(char[] password) {
        return BCRYPT.encode(CharBuffer.wrap(password));
    }
}
