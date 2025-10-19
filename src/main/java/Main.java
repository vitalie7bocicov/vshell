import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class Main {

    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("$ ");
            String[] input = scanner.nextLine().split(" ");
            String inputCmd = input[0];
            List<String> params = new ArrayList<>(Arrays.asList(input).subList(1, input.length));

            try {
                COMMANDS cmd = COMMANDS.fromString(inputCmd);
                switch (cmd) {
                    case EXIT:  {
                        return;
                    }
                    case ECHO: {
                        System.out.println(String.join(" ", params));
                        break;
                    }
                    case TYPE: {
                        String param = params.getFirst();
                        String execPath;
                        if (COMMANDS.isValid(param)) {
                            System.out.println(param + " is a shell builtin");
                        } else if ((execPath = isInPathAndHasRights(param)) != null) {
                            System.out.println(param + " is " + execPath);
                        } else {
                            System.out.println(param + ": not found");
                        }
                        break;
                    }
                    default: {
                    }
                }
            } catch (IllegalAccessException e) {
                System.out.println(input[0] + ": command not found");
            }
        }
    }

    private static String[] getPaths() {
        return System.getenv("PATH").split(File.pathSeparator);
    }

    private static String isInPathAndHasRights(String param) {
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
}
