package app;

import completion.Autocomplete;
import executor.PipelineExecutor;
import history.History;
import model.COMMANDS;
import executor.BultinCmdExecutor;
import executor.ExternalCmdExecutor;
import util.TerminalUtil;
import util.Tokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VshellApp {

    private Path currentWorkingDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
    
    public void start() throws IOException, InterruptedException {
        History history = new History();
        int historyIndex = 0;
        TerminalUtil.setTerminalRawMode();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            StringBuilder currentLine = new StringBuilder();
            TerminalUtil.resetLine(currentLine);
            while (true) {
                int input = reader.read();
                if (input == -1) break;
                char c = (char) input;

                if (c == '\n' || c == '\r') {
                    TerminalUtil.println("");
                    String line = currentLine.toString();
                    history.addCommand(line);
                    historyIndex = 0;
                    List<String> params = Tokenizer.tokenize(line);
                    if (line.contains("|")) {
                        List<List<String>> stages = extractStages(params);
                        PipelineExecutor.run(this, stages);
                        TerminalUtil.resetLine(currentLine);
                        continue;
                    }
                    if (params.isEmpty()) {
                        TerminalUtil.resetLine(currentLine);
                        continue;
                    }
                    String commandName = params.getFirst();
                    params = params.subList(1, params.size());
                    switch (COMMANDS.fromString(commandName)) {
                        case EXIT:  {
                            return;
                        }
                        case ECHO: {
                            BultinCmdExecutor.executeEcho(params);
                            break;
                        }
                        case TYPE: {
                            BultinCmdExecutor.executeType(params);
                            break;
                        }
                        case PWD: {
                            TerminalUtil.println(currentWorkingDir.toString());
                            break;
                        }
                        case CD: {
                            BultinCmdExecutor.executeCD(this, params);
                            break;
                        }
                        case HISTORY: {
                            int n = params.isEmpty() ? 0 : Integer.parseInt(params.getFirst());
                            BultinCmdExecutor.executeHistory(history, n);
                            break;
                        }
                        case EXTERNAL: {
                            ExternalCmdExecutor.runExternalProgram(commandName, params);
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                    TerminalUtil.resetLine(currentLine);

                } else if (c == '\t') {
                    Autocomplete.handleTabPress(currentLine);
                } else if (c == 127 || c == 8) { // Backspace
                    if (!currentLine.isEmpty()) {
                        currentLine.setLength(currentLine.length() - 1);
                        System.out.print("\b \b");
                    }
                } else if (c == 27) { // ESC
                    var c2 = reader.read();
                    if (c2 == '[') {
                        int c3 = reader.read();
                        if (c3 == 'A') { // UP ARROW
                            historyIndex++;
                            TerminalUtil.resetLine(currentLine);
                            String cmd = history.getUpArrow(historyIndex);
                            currentLine.append(cmd);
                            System.out.print(cmd);
                        } else if (c3 == 'B') { // DOWN ARROW
                            historyIndex--;
                            TerminalUtil.resetLine(currentLine);
                            String cmd = history.getDownArrow(historyIndex);
                            currentLine.append(cmd);
                            System.out.print(cmd);
                        }
                    }

                } else {
                    currentLine.append(c);
                    System.out.print(c);
                }
                System.out.flush();
            }
        } finally {
            TerminalUtil.resetTerminalMode();
        }

    }

    public Path getCurrentWorkingDir() {
        return currentWorkingDir;
    }

    public void setCurrentWorkingDir(Path currentWorkingDir) {
        this.currentWorkingDir = currentWorkingDir;
    }

    private static List<List<String>> extractStages(List<String> params) {
        List<List<String>> stages = new ArrayList<>();
        List<String> current = new ArrayList<>();
        for (String token : params) {
            if (token.equals("|")) {
                stages.add(current);
                current = new ArrayList<>();
            } else {
                current.add(token);
            }
        }
        stages.add(current);
        return stages;
    }

}
