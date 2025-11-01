import java.io.IOException;
import java.util.List;

public class ExternalProgramExecutor {

    public static void execute(List<String> command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.inheritIO();
        Process process = processBuilder.start();
        process.waitFor();
    }

}
