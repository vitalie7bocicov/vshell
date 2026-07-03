package app;

import completion.Autocomplete;
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
import java.util.List;

public class VshellApp {

    private Path currentWorkingDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
    
    public void start() throws IOException, InterruptedException {
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
                    List<String> params = Tokenizer.tokenize(line);
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
                            System.out.println(this.currentWorkingDir);
                            break;
                        }
                        case CD: {
                            BultinCmdExecutor.executeCD(this, params);
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
                        System.out.flush();
                    }
                } else {
                    currentLine.append(c);
                    System.out.print(c);
                    System.out.flush();
                }
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

}
