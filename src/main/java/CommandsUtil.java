import javax.xml.xpath.XPath;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CommandsUtil {

    static void runExternalProgram(String inputCmd, List<String> params) throws IOException, InterruptedException {
        String execPath = FileUtil.isInPathAndHasRights(inputCmd);
        if (execPath != null) {
            List<String> fullCmd = new ArrayList<>();
            fullCmd.add(inputCmd);
            fullCmd.addAll(params);
            ExternalProgramExecutor.execute(fullCmd);
        } else {
            System.out.println(inputCmd + ": command not found");
        }
    }

    static void executeTypeCmd(List<String> params) {
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
        String param = params.getFirst();
        Path targetPath = shell.currentWorkingDir.resolve(param).normalize();
        try {
            if (!Files.isDirectory(targetPath)) {
                System.out.println("cd: " + param + ": No such file or directory");
                return;
            }
            shell.currentWorkingDir = targetPath.toAbsolutePath().normalize();
        } catch (SecurityException e) {
            System.out.println("cd: " + param + ": Permission denied");
        }

    }
}
