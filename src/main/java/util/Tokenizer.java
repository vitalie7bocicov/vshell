package util;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    public static final char SINGLE_QUOTE = '\'';
    public static final char DOUBLE_QUOTE = '\"';
    public static final char BACKSLASH = '\\';

    public static List<String> tokenize(String rawParams) {
        List<String> params = new ArrayList<>();
        var sb = new StringBuilder();
        boolean inSingleQuotes = false;
        boolean inDoubleQuotes = false;
        boolean escapeChar = false;
        for (int i = 0; i < rawParams.length(); i++) {
            char c = rawParams.charAt(i);
            if (c == BACKSLASH && !inDoubleQuotes && !inSingleQuotes) {
                escapeChar = true;
            } else if (SINGLE_QUOTE == c && !inDoubleQuotes && !escapeChar) {
                inSingleQuotes = !inSingleQuotes;
            } else if (DOUBLE_QUOTE == c && !inSingleQuotes && !escapeChar) {
                inDoubleQuotes = !inDoubleQuotes;
            } else if (Character.isWhitespace(c) && !escapeChar) {
                if (inSingleQuotes || inDoubleQuotes) {
                    sb.append(rawParams.charAt(i));
                } else {
                    if (!sb.isEmpty()) {
                        params.add(sb.toString());
                        sb.setLength(0);
                    }
                }
            } else if (c == BACKSLASH && inDoubleQuotes && !escapeChar && charIsEscapable(i + 1, rawParams)) {
                escapeChar = true;
            } else {
                sb.append(c);
                escapeChar = false;
            }
        }
        if (!sb.isEmpty()) {
            params.add(sb.toString());
        }
        return params;
    }

    private static boolean charIsEscapable(int i, String rawParams) {
        if (i >= rawParams.length()) {
            return false;
        }
        return rawParams.charAt(i) == BACKSLASH || rawParams.charAt(i) == DOUBLE_QUOTE;
    }
}
