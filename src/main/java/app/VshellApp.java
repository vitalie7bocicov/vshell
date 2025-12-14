package app;

import model.COMMANDS;
import executor.BultinCmdExecutor;
import executor.ExternalCmdExecutor;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class VshellApp {

    private Path currentWorkingDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
    
    public void start() throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine();
            String[] args = input.split("\\s+");
            String inputCmd = args[0];
            List<String> params = new ArrayList<>(Arrays.asList(args).subList(1, args.length));
            switch (COMMANDS.fromString(inputCmd)) {
                case EXIT:  {
                    return;
                }
                case ECHO: {
                    String param = input.substring(inputCmd.length() + 1);
                    BultinCmdExecutor.executeEcho(param);
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
                    ExternalCmdExecutor.runExternalProgram(input);
                    break;
                }
                default: {
                    break;
                }
            }
        }
        
    }

    public Path getCurrentWorkingDir() {
        return currentWorkingDir;
    }

    public void setCurrentWorkingDir(Path currentWorkingDir) {
        this.currentWorkingDir = currentWorkingDir;
    }
}
