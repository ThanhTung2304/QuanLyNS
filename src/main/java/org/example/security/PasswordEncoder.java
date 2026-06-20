package org.example.security;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordEncoder {

    public static String encode(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(10));
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }

        // BCrypt hash hợp lệ thường bắt đầu bằng $2a$, $2b$, $2y$ và dài khoảng 60 ký tự
        if (!encodedPassword.startsWith("$2a$")
                && !encodedPassword.startsWith("$2b$")
                && !encodedPassword.startsWith("$2y$")) {
            return false;
        }

        try {
            return BCrypt.checkpw(rawPassword, encodedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}