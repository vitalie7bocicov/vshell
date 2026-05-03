package completion;

import util.TerminalUtil;

import java.util.List;

public class Autocomplete {

    public static Trie trie = new Trie();

    static {
        trie.insert("echo");
        trie.insert("exit");
    }

    public static void handleTabPress(StringBuilder input) {
        String prefix = input.toString();
        List<String> completions = trie.getWordsWithPrefix(prefix);

        if (completions.isEmpty()) {
            TerminalUtil.print("\u0007");
            return;
        }
        if (completions.size() == 1) {
            String suffix = completions.getFirst().substring(prefix.length()) + " ";
            TerminalUtil.print(suffix);
            input.append(suffix);
        } else {
            TerminalUtil.println(String.join("\r\n", completions));
            TerminalUtil.print("$ " + input);
        }
    }
}
