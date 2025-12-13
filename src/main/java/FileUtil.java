import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

    static String isInPathAndHasRights(String param) {
        for (String path : getPaths()) {
            String execPath = fileExistsAndIsExecutable(param, new File(path));
            if (execPath != null) {
                return execPath;
            }
        }
        return null;
    }

    static String getHomePath() {
        return System.getenv("HOME");
    }

    private static String fileExistsAndIsExecutable(String name, File file) {
        File[] list = file.listFiles();
        if (list == null) {
            return null;
        }
        for (File f : list) {
            if (name.equalsIgnoreCase(f.getName())) {
                if (f.canExecute()) {
                    return f.getAbsolutePath();
                }
            }
        }
        return null;
    }

    private static String[] getPaths() {
        return System.getenv("PATH").split(File.pathSeparator);
    }

    public static void writeToPath(String location, String output) {
        Path filePath = Paths.get(location);
        String newLine = System.lineSeparator();
        String content = output + newLine;
        try {
            Files.writeString(filePath, content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createFile(String location) {
        try {
            Files.createFile(Path.of(location));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
