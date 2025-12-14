package util;

import model.RedirectMode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileUtil {

    public static String isInPathAndHasRights(String param) {
        for (String path : getPaths()) {
            String execPath = fileExistsAndIsExecutable(param, new File(path));
            if (execPath != null) {
                return execPath;
            }
        }
        return null;
    }

    public static String getHomePath() {
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

    public static void writeToPath(Path location, String output, RedirectMode redirectMode) {
        String newLine = System.lineSeparator();
        String content = output + newLine;
        try {
            createFileIfNotExists(location);
            switch (redirectMode) {
                case APPEND -> Files.write(location, content.getBytes(), StandardOpenOption.APPEND);
                case TRUNCATE -> Files.write(location, content.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createFile(Path location) {
        try {
            Files.createFile(location);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createFileIfNotExists(Path out) {
        Path parent = out.getParent();
        if (parent != null && Files.notExists(parent)) {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (Files.notExists(out)) {
            try {
                Files.createFile(out);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
