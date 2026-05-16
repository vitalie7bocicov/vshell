package completion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trie {
    private final Map<Character, Trie> children = new HashMap<>();
    private boolean isWord;

    public Trie() {
    }

    public void insert(String word) {
        Trie node = this;
        for (char c : word.toCharArray()) {
            node = node.children.computeIfAbsent(c, _ -> new Trie());
        }
        node.isWord = true;
    }

    public boolean startsWith(String prefix) {
        Trie node = this;
        for (char c : prefix.toCharArray()) {
            node = node.children.getOrDefault(c, null);
            if (node == null) {
                return false;
            }
        }
        return true;
    }

    public List<String> getWordsWithPrefix(String prefix) {
        List<String> completions = new ArrayList<>();
        Trie node = this;

        for (char c : prefix.toCharArray()) {
            node = node.children.getOrDefault(c, null);
            if (node == null) {
                return completions;
            }
        }

        dfs(node, new StringBuilder(prefix), completions);
        return completions;
    }

    private void dfs(Trie node, StringBuilder currentWord, List<String> completions) {
        if (node.isWord) {
            completions.add(currentWord.toString());
        }
        for (Character ch : node.children.keySet()) {
            currentWord.append(ch);
            dfs(node.children.get(ch), currentWord, completions);
            currentWord.deleteCharAt(currentWord.length() - 1);
        }
    }

}
