import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CommandsUtil {

    public static final char SINGLE_QUOTE = '\'';
    public static final char DOUBLE_QUOTE = '\"';
    public static final char BACKSLASH = '\\';

    static void runExternalProgram(String input) throws IOException, InterruptedException {
        List<String> tokens = tokenize(input);
        String cmd = tokens.getFirst();
        tokens = tokens.subList(1, tokens.size());
        String execPath = FileUtil.isInPathAndHasRights(cmd);
        if (execPath == null) {
            System.out.println(cmd + ": command not found");
            return;
        }
        List<String> fullCmd = new ArrayList<>();
        fullCmd.add(cmd);
        if (tokens.isEmpty()) {
            ExternalProgramExecutor.execute(fullCmd);
            return;
        }
        fullCmd.addAll(tokens);
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
        boolean escapeChar = false;
        for (int i = 0; i < rawParams.length(); i++) {
            char c = rawParams.charAt(i);
            if (c == BACKSLASH && !inDoubleQuotes && !inSingleQuotes) {
                escapeChar = true;
            } else if (SINGLE_QUOTE == c && !inDoubleQuotes && !escapeChar) {
                inSingleQuotes = !inSingleQuotes;
            } else if (DOUBLE_QUOTE == c && !inSingleQuotes && !escapeChar) {
                inDoubleQuotes = !inDoubleQuotes;
            } else if (Character.isWhitespace(c) && !escapeChar) {
                if (inSingleQuotes || inDoubleQuotes) {
                    sb.append(rawParams.charAt(i));
                } else {
                    if (!sb.isEmpty()) {
                        params.add(sb.toString());
                        sb.setLength(0);
                    }
                }
            } else if (c == BACKSLASH && inDoubleQuotes && !escapeChar && charIsEscapable(i + 1, rawParams)) {
                escapeChar = true;
            } else {
                sb.append(c);
                escapeChar = false;
            }
        }
        if (!sb.isEmpty()) {
            params.add(sb.toString());
        }
        return params;
    }

    private static boolean charIsEscapable(int i, String rawParams) {
        if (i >= rawParams.length()) {
            return false;
        }
        return rawParams.charAt(i) == BACKSLASH || rawParams.charAt(i) == DOUBLE_QUOTE;
    }
}
