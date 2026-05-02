package util;

public class TerminalUtil {

    public static void println(String message) {
        System.out.print("\r" + message + "\r\n");
        System.out.flush();
    }

    public static void print(String message) {
        System.out.print(message);
        System.out.flush();
    }

    public static void setTerminalRawMode() {
        String[] cmd = {"/bin/sh", "-c", "stty -echo raw < /dev/tty"};
        try {
            Process process = Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            System.err.println("Error: Could not reset terminal mode. " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void resetTerminalMode() {
        String[] cmd = {"/bin/sh", "-c", "stty sane < /dev/tty"};
        try {
            Runtime.getRuntime().exec(cmd).waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void resetLine(StringBuilder currentLine) {
        currentLine.setLength(0);
        System.out.print("\r$ ");
        System.out.flush();
    }
}
