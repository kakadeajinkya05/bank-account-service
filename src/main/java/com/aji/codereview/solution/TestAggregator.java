package com.aji.codereview.solution;

public class TestAggregator {
    public static void main(String[] args) {
        String pattern = "^ACC{3}\\d{5}$";

        String testString = "ACC1234";
        patternMatching(testString, pattern);

        testString = "AAC1234";
        patternMatching(testString, pattern);

        testString = "CCC1234";
        patternMatching(testString, pattern);

        testString = "ACC123";
        patternMatching(testString, pattern);

        float value = Float.MAX_VALUE * 1.1f;
        floatValidation(value);
        value = Float.MAX_VALUE * 2;
        floatValidation(value);
        value = Float.MAX_VALUE + 1e38f;
        floatValidation(value);
        value = Float.MAX_VALUE - (-1e38f);
        floatValidation(value);
        value = Float.MAX_VALUE * 10;
        floatValidation(value);
        value = 1e38f / 1e-38f;
        floatValidation(value);
        value = Float.MAX_VALUE + Float.POSITIVE_INFINITY;
        floatValidation(value);
        value = Float.MAX_VALUE * Float.POSITIVE_INFINITY;
        floatValidation(value);
        value = Float.POSITIVE_INFINITY + 1;
        floatValidation(value);
        value = Float.POSITIVE_INFINITY * 2;
        floatValidation(value);
        value = Float.POSITIVE_INFINITY - 1;
        floatValidation(value);
        value = Float.POSITIVE_INFINITY / 2;
        floatValidation(value);
        value = 12.34f;
        floatValidation(value);
        value = -12.34f;
        floatValidation(value);

    }

    private static void patternMatching(String testString, String pattern) {
        boolean matches = testString.matches(pattern);

        patternMatching(matches, testString);
    }

    private static void patternMatching(boolean matches, String testString) {
        if (matches) {
            System.out.println(testString + " matches the pattern.");
        } else {
            System.out.println(testString + " does not match the pattern.");
        }
    }

    private static void floatValidation(float value) {
        if (value != Float.POSITIVE_INFINITY) {
            System.err.println("valid amount");
        } else {
            System.err.println("Invalid amount");
        }
    }
}
