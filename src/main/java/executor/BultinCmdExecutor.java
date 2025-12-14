package executor;

import app.VshellApp;
import model.COMMANDS;
import model.Redirect;
import util.FileUtil;
import util.RedirectParser;
import util.Tokenizer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class BultinCmdExecutor {

    public static final String SPACE = " ";

    public static void executeType(List<String> params) {
        String param = params.getFirst();
        String execPath;
        if (!COMMANDS.fromString(param).equals(COMMANDS.EXTERNAL)) {
            System.out.println(param + " is a shell builtin");
        } else if ((execPath = FileUtil.isInPathAndHasRights(param)) != null) {
            System.out.println(param + " is " + execPath);
        } else {
            System.out.println(param + ": not found");
        }
    }

    public static void executeCD(VshellApp shell, List<String> params) {
        String path = params.getFirst();
        if (path.equals("~")) {
            path = FileUtil.getHomePath();
        }
        Path targetPath = shell.getCurrentWorkingDir().resolve(path).normalize();
        try {
            if (!Files.isDirectory(targetPath)) {
                System.out.println("cd: " + path + ": No such file or directory");
                return;
            }
            shell.setCurrentWorkingDir(targetPath.toAbsolutePath().normalize());
        } catch (SecurityException e) {
            System.out.println("cd: " + path + ": Permission denied");
        }

    }

    public static void executeEcho(String param) {
        List<String> tokens = Tokenizer.tokenize(param);
        Optional<Redirect> redirectOpt = RedirectParser.parse(tokens);
        if (redirectOpt.isEmpty()) {
            System.out.println(String.join(SPACE, tokens));
            return;
        }
        Redirect redirect = redirectOpt.get();
        String output = String.join(SPACE, tokens.subList(0, redirect.operatorIndex()));
        switch (redirect.type()) {
            case STDOUT -> FileUtil.writeToPath(redirect.path(), output, redirect.mode());
            case STDERR -> {
                System.out.println(output);
                FileUtil.createFile(redirect.path());
            }
        }
    }
}
