package util;

import model.RedirectMode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileUtil {

    public static String isInPathAndHasRights(String param) {
        for (String path : getSystemPathDirectories()) {
            String execPath = fileExistsAndIsExecutable(param, new File(path));
            if (execPath != null) {
                return execPath;
            }
        }
        return null;
    }

    public static String[] getSystemPathDirectories() {
        String pathEnv = System.getenv("PATH");
        if (pathEnv == null || pathEnv.isEmpty()) {
            return new String[0];
        }
        return pathEnv.split(File.pathSeparator);
    }

    public static String getHomePath() {
        return System.getenv("HOME");
    }

    public static Set<String> getExecutablesFromPathEnv() {
        Set<String> executables = new HashSet<>();
        for (String pathDir : getSystemPathDirectories()) {
            File directory = new File(pathDir);
            File[] files = directory.listFiles();

            if (files == null) continue;
            for (var file : files) {
                if (file.isFile() && file.canExecute()) {
                    executables.add(file.getName());
                }
            }
        }
        return executables;
    }

    public static String fileExistsAndIsExecutable(String name, File file) {
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
