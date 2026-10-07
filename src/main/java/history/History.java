package history;

import java.util.ArrayList;
import java.util.List;

public class History {

    private final List<String> history = new ArrayList<>();

    public void addCommand(String command) {
        history.add(command);
    }

    public List<String> getHistory() {
        return history;
    }

    public String getUpArrow(int historyIndex) {
        return history.get(Math.max(0, history.size() - historyIndex));
    }
}
