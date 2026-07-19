package completion;

import util.FileUtil;
import util.TerminalUtil;

import java.util.List;

public class Autocomplete {

    private static final Trie trie = new Trie();
    private static boolean ringSwitch =  false;


    static {
        trie.insert("echo");
        trie.insert("exit");
        for (var exec : FileUtil.getExecutablesFromPathEnv()) {
            trie.insert(exec);
        }
    }

    public static void handleTabPress(StringBuilder input) {
        ringSwitch = !ringSwitch;
        String prefix = input.toString();
        List<String> completions = trie.getWordsWithPrefix(prefix);

        if (ringSwitch) {
            TerminalUtil.print("\u0007");
        }
        if (completions.size() == 1) {
            String suffix = completions.getFirst().substring(prefix.length()) + " ";
            TerminalUtil.print(suffix);
            input.append(suffix);
        } else {
            // multiple matches, find the longest common prefix
            String lcp = LongestCommonPrefix.findLongestCommonPrefix(completions);
            if (lcp.isEmpty()) {
                TerminalUtil.print("\u0007");
                return;
            }
            if (lcp.equals(prefix)) {
                TerminalUtil.println("");
                TerminalUtil.println(String.join(" ", completions));
                TerminalUtil.print("$ " + prefix);
            } else {
                String suffix = lcp.substring(input.length());
                TerminalUtil.print(suffix);
                input.append(suffix);
            }
        }
    }
}
