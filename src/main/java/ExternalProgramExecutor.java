import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;

public class ExternalProgramExecutor {

    public static void execute(List<String> command, Path redirectFile) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);

        if (redirectFile != null) {
            processBuilder.redirectOutput(redirectFile.toFile());
            processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
        } else {
            processBuilder.inheritIO();
        }
        Process process = processBuilder.start();
//        process.getOutputStream().close();
        process.waitFor();
    }

}
