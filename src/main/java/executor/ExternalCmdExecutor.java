package executor;

import model.Redirect;
import model.RedirectMode;
import util.FileUtil;
import util.RedirectParser;
import util.Tokenizer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExternalCmdExecutor {

    public static void runExternalProgram(String input) throws IOException, InterruptedException {
        List<String> fullTokens = Tokenizer.tokenize(input);
        String cmd = fullTokens.getFirst();
        List<String> tokens = fullTokens.subList(1, fullTokens.size());
        if (FileUtil.isInPathAndHasRights(cmd) == null) {
            System.err.println(cmd + ": command not found");
            return;
        }
        List<String> parsedCmd = new ArrayList<>();
        parsedCmd.add(cmd);
        Optional<Redirect> redirectOptional = RedirectParser.parse(tokens);
        if (redirectOptional.isEmpty()) {
            parsedCmd.addAll(tokens);
            ExternalCmdExecutor.execute(parsedCmd);
            return;
        }

        Redirect redirect = redirectOptional.get();
        parsedCmd.addAll(tokens.subList(0, redirect.operatorIndex()));

        switch (redirect.type()) {
            case STDOUT -> ExternalCmdExecutor.executeWithRedirectedOutput(parsedCmd, redirect.path(), redirect.mode());
            case STDERR -> ExternalCmdExecutor.executeWithRedirectedError(parsedCmd, redirect.path(), redirect.mode());
        }
    }

    public static void execute(List<String> cmds) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(cmds);
        processBuilder.inheritIO();
        Process process = processBuilder.start();
        process.waitFor();
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
