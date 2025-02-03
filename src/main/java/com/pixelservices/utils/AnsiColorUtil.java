package com.pixelservices.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnsiColorUtil {
    private static final String HEX_COLOR_PATTERN = "#[a-fA-F0-9]{6}";
    private static final String ANSI_PATTERN = "\\u001B\\[[;\\d]*m";

    public static String applyColors(String message) {
        Pattern pattern = Pattern.compile(HEX_COLOR_PATTERN);
        Matcher matcher = pattern.matcher(message);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            String hexColor = matcher.group();
            String ansiColor = hexToAnsi(hexColor);
            matcher.appendReplacement(buffer, ansiColor);
        }
        matcher.appendTail(buffer);
        return buffer + "\u001B[0m";
    }

    public static String hexToAnsi(String hexColor) {
        if (hexColor == null || !hexColor.startsWith("#") || hexColor.length() != 7) {
            throw new IllegalArgumentException("Invalid hex color code.");
        }

        int r = Integer.parseInt(hexColor.substring(1, 3), 16);
        int g = Integer.parseInt(hexColor.substring(3, 5), 16);
        int b = Integer.parseInt(hexColor.substring(5, 7), 16);

        return String.format("\u001B[38;2;%d;%d;%dm", r, g, b);
    }

    public static String stripAnsiCodes(String message) {
        return message.replaceAll(ANSI_PATTERN, "");
    }
}
