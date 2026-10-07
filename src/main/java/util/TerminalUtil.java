package util;

import java.io.PrintStream;

public class TerminalUtil {

    private static final ThreadLocal<PrintStream> OUT = ThreadLocal.withInitial(() -> System.out);
    private static volatile boolean rawMode;

    public static void setOut(PrintStream out) {
        OUT.set(out);
    }

    public static void clearOut() {
        OUT.remove();
    }

    public static void println(String message) {
        PrintStream out = OUT.get();
        if (out == System.out && rawMode) {
            out.print("\r" + message + "\r\n");
        } else {
            out.println(message);
        }
        out.flush();
    }

    public static void print(String message) {
        PrintStream out = OUT.get();
        out.print(message);
        out.flush();
    }

    public static void setTerminalRawMode() {
        String[] cmd = {"/bin/sh", "-c", "stty -echo raw < /dev/tty"};
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            rawMode = true;
        } catch (Exception e) {
            System.err.println("Error: Could not reset terminal mode. " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void resetTerminalMode() {
        String[] cmd = {"/bin/sh", "-c", "stty sane < /dev/tty"};
        try {
            Runtime.getRuntime().exec(cmd).waitFor();
            rawMode = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void resetLine(StringBuilder currentLine) {
        currentLine.setLength(0);
        System.out.print("\r\u001b[K$ ");
        System.out.flush();
    }
}
