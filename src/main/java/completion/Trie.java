package completion;

import java.util.ArrayList;
import java.util.List;

public class Trie {
    private Trie[] letters = new Trie[26];
    private boolean isWord;

    public Trie() {
    }

    public void insert(String word) {
        Trie node = this;
        for (char c : word.toCharArray()) {
            int index = c - 'a';
            if (node.letters[index] == null) {
                node.letters[index] = new Trie();
            }
            node = node.letters[index];
        }
        node.isWord = true;
    }

    public boolean startsWith(String prefix) {
        Trie node = this;
        for (char c : prefix.toCharArray()) {
            int index = c - 'a';
            if (index < 0 || index >= 26 || node.letters[index] == null) {
                return false;
            }
            node = node.letters[index];
        }
        return true;
    }

    public List<String> getWordsWithPrefix(String prefix) {
        List<String> completions = new ArrayList<>();
        Trie node = this;

        for (char c : prefix.toCharArray()) {
            int index = c - 'a';
            if (index < 0 || index >= 26 || node.letters[index] == null) {
                return completions;
            }
            node = node.letters[index];
        }

        dfs(node, new StringBuilder(prefix), completions);
        return completions;
    }

    private void dfs(Trie node, StringBuilder currentWord, List<String> completions) {
        if (node.isWord) {
            completions.add(currentWord.toString());
        }
        for (int i = 0; i < 26; i++) {
            if (node.letters[i] != null) {
                currentWord.append((char) (i + 'a'));
                dfs(node.letters[i], currentWord, completions);
                currentWord.deleteCharAt(currentWord.length() - 1);
            }
        }
    }

}
