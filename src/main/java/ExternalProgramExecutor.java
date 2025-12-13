import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;

public class ExternalProgramExecutor {

    public static void execute(List<String> cmds) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(cmds);
        processBuilder.inheritIO();
        Process process = processBuilder.start();
        process.waitFor();
    }


    public static void executeWithRedirectedOut(List<String> cmds, Path redirectOut) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(cmds);
        processBuilder.redirectOutput(redirectOut.toFile());
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process process = processBuilder.start();
        process.waitFor();
    }

    public static void executeWithRedirectedErr(List<String> cmds, Path redirectErr) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(cmds);
        processBuilder.redirectError(redirectErr.toFile());
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        Process process = processBuilder.start();
        process.waitFor();
    }
}
