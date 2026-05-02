package executor;

import model.Redirect;
import model.RedirectMode;
import util.FileUtil;
import util.RedirectParser;
import util.TerminalUtil;
import util.Tokenizer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExternalCmdExecutor {

    public static void runExternalProgram(String cmd, List<String> params) throws IOException, InterruptedException {
        if (FileUtil.isInPathAndHasRights(cmd) == null) {
            TerminalUtil.println(cmd + ": command not found");
            return;
        }
        List<String> parsedCmd = new ArrayList<>();
        parsedCmd.add(cmd);
        Optional<Redirect> redirectOptional = RedirectParser.parse(params);
        if (redirectOptional.isEmpty()) {
            parsedCmd.addAll(params);
            ExternalCmdExecutor.execute(parsedCmd);
            return;
        }

        Redirect redirect = redirectOptional.get();
        parsedCmd.addAll(params.subList(0, redirect.operatorIndex()));

        switch (redirect.type()) {
            case STDOUT -> ExternalCmdExecutor.executeWithRedirectedOutput(parsedCmd, redirect.path(), redirect.mode());
            case STDERR -> ExternalCmdExecutor.executeWithRedirectedError(parsedCmd, redirect.path(), redirect.mode());
        }
    }

    public static void execute(List<String> cmds) throws IOException, InterruptedException {
        TerminalUtil.resetTerminalMode();
        ProcessBuilder processBuilder = new ProcessBuilder(cmds);
        processBuilder.inheritIO();
        Process process = processBuilder.start();
        process.waitFor();
        TerminalUtil.setTerminalRawMode();
    }


    public static void executeWithRedirectedOutput(List<String> cmds,
                                                   Path out,
                                                   RedirectMode mode) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(cmds);
        switch (mode) {
            case TRUNCATE -> processBuilder.redirectOutput(out.toFile());
            case APPEND -> {
                FileUtil.createFileIfNotExists(out);
                processBuilder.redirectOutput(ProcessBuilder.Redirect.appendTo(out.toFile()));
            }
        }
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process process = processBuilder.start();
        process.waitFor();
    }

    public static void executeWithRedirectedError(List<String> cmds,
                                                  Path out,
                                                  RedirectMode mode) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(cmds);
        switch (mode) {
            case TRUNCATE -> processBuilder.redirectError(out.toFile());
            case APPEND -> {
                FileUtil.createFileIfNotExists(out);
                processBuilder.redirectError(ProcessBuilder.Redirect.appendTo(out.toFile()));
            }
        }
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        Process process = processBuilder.start();
        process.waitFor();
    }
}
