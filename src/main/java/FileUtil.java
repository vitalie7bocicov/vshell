import java.io.File;

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

}
