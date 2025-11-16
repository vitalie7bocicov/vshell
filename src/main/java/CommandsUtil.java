import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CommandsUtil {

    public static final char SINGLE_QUOTE = '\'';
    public static final char DOUBLE_QUOTE = '\"';

    static void runExternalProgram(String inputCmd, String rawParams) throws IOException, InterruptedException {
        String execPath = FileUtil.isInPathAndHasRights(inputCmd);
        if (execPath == null) {
            System.out.println(inputCmd + ": command not found");
            return;
        }
        List<String> fullCmd = new ArrayList<>();
        fullCmd.add(inputCmd);
        if (rawParams.isBlank()) {
            ExternalProgramExecutor.execute(fullCmd);
            return;
        }
        List<String> params = tokenize(rawParams);
        fullCmd.addAll(params);
        ExternalProgramExecutor.execute(fullCmd);
    }


    static void executeType(List<String> params) {
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
        Path targetPath = shell.currentWorkingDir.resolve(path).normalize();
        try {
            if (!Files.isDirectory(targetPath)) {
                System.out.println("cd: " + path + ": No such file or directory");
                return;
            }
            shell.currentWorkingDir = targetPath.toAbsolutePath().normalize();
        } catch (SecurityException e) {
            System.out.println("cd: " + path + ": Permission denied");
        }

    }

    public static void executeEcho(String param) {
        System.out.println(String.join(" ", tokenize(param)));
    }

    private static List<String> tokenize(String rawParams) {
        List<String> params = new ArrayList<>();
        var sb = new StringBuilder();
        boolean inSingleQuotes = false;
        boolean inDoubleQuotes = false;
        for (int i = 0; i < rawParams.length(); i++) {
            char c = rawParams.charAt(i);
            if (SINGLE_QUOTE == c && !inDoubleQuotes) {
                inSingleQuotes = !inSingleQuotes;
            } else if (DOUBLE_QUOTE == c && !inSingleQuotes) {
                inDoubleQuotes = !inDoubleQuotes;
            } else if (Character.isWhitespace(c)) {
                if (inSingleQuotes || inDoubleQuotes) {
                    sb.append(rawParams.charAt(i));
                } else {
                    if (!sb.isEmpty()) {
                        params.add(sb.toString());
                        sb.setLength(0);
                    }
                }
            } else {
                sb.append(c);
            }
        }
        if (!sb.isEmpty()) {
            params.add(sb.toString());
        }
        return params;
    }
}
