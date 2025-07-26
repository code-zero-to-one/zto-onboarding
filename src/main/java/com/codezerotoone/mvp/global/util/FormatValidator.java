package com.codezerotoone.mvp.global.util;

import java.util.regex.Pattern;

public class FormatValidator {
    public static boolean isValid(CharSequence value, Pattern pattern) {
        return hasValue(value) && hasValue(pattern) && pattern.matcher(value).matches();
    }

    public static boolean hasValue(Object value) {
        return value != null && !value.toString().trim().isEmpty();
    }
}
