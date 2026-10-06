package completion;

import util.FileUtil;
import util.TerminalUtil;

import java.util.List;

public class Autocomplete {

    private static final Trie trie = new Trie();
    private static String bellPrefix;


    static {
        trie.insert("echo");
        trie.insert("exit");
        for (var exec : FileUtil.getExecutablesFromPathEnv()) {
            trie.insert(exec);
        }
    }

    public static void handleTabPress(StringBuilder input) {
        String prefix = input.toString();
        List<String> completions = trie.getWordsWithPrefix(prefix);

        if (completions.isEmpty()) {
            ringBell(prefix);
            return;
        }
        if (completions.size() == 1) {
            String suffix = completions.getFirst().substring(prefix.length()) + " ";
            TerminalUtil.print(suffix);
            input.append(suffix);
            bellPrefix = null;
            return;
        }

        String lcp = LongestCommonPrefix.findLongestCommonPrefix(completions);
        if (lcp.length() > prefix.length()) {
            String suffix = lcp.substring(prefix.length());
            TerminalUtil.print(suffix);
            input.append(suffix);
            bellPrefix = null;
        } else if (!prefix.equals(bellPrefix)) {
            ringBell(prefix);
        } else {
            TerminalUtil.println("");
            TerminalUtil.println(String.join(" ", completions));
            TerminalUtil.print("$ " + prefix);
            bellPrefix = null;
        }
    }

    private static void ringBell(String prefix) {
        TerminalUtil.print("\u0007");
        bellPrefix = prefix;
    }
}
