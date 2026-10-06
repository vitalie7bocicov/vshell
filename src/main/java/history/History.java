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
}
