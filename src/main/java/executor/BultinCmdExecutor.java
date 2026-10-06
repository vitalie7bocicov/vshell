package executor;

import app.VshellApp;
import history.History;
import model.COMMANDS;
import model.Redirect;
import org.xml.sax.helpers.XMLFilterImpl;
import util.FileUtil;
import util.RedirectParser;
import util.TerminalUtil;
import util.Tokenizer;

import java.io.PrintStream;
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
            TerminalUtil.println(param + " is a shell builtin");
        } else if ((execPath = FileUtil.isInPathAndHasRights(param)) != null) {
            TerminalUtil.println(param + " is " + execPath);
        } else {
            TerminalUtil.println(param + ": not found");
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
                TerminalUtil.println("cd: " + path + ": No such file or directory");
                return;
            }
            shell.setCurrentWorkingDir(targetPath.toAbsolutePath().normalize());
        } catch (SecurityException e) {
            TerminalUtil.println("cd: " + path + ": Permission denied");
        }
    }

    public static void executeEcho(List<String> params) {
        Optional<Redirect> redirectOpt = RedirectParser.parse(params);
        if (redirectOpt.isEmpty()) {
            TerminalUtil.println(String.join(SPACE, params));
            return;
        }
        Redirect redirect = redirectOpt.get();
        String output = String.join(SPACE, params.subList(0, redirect.operatorIndex()));
        switch (redirect.type()) {
            case STDOUT -> FileUtil.writeToPath(redirect.path(), output, redirect.mode());
            case STDERR -> {
                TerminalUtil.println(output);
                FileUtil.createFile(redirect.path());
            }
        }
    }

    public static void executeHistory(History history) {
        List<String> commands = history.getHistory();
        for (int i = 0; i < commands.size(); i++) {
            TerminalUtil.println((i + 1) + " " + commands.get(i));
        }
    }
}
