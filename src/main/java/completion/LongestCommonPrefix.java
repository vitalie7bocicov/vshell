package completion;

import java.util.List;

public class LongestCommonPrefix {

    public static String findLongestCommonPrefix(List<String> matches) {
        if (matches == null || matches.isEmpty()) return "";
        String prefix = matches.getFirst();
        for (int i = 1; i < matches.size(); i++) {
            String current = matches.get(i);
            while (!current.startsWith(prefix)) {
                prefix = prefix.substring(0, prefix.length() - 1);
                if (prefix.isEmpty()) {
                    return "";
                }
            }
        }
        return prefix;
    }
}
