package executor;

import app.VshellApp;
import model.COMMANDS;
import util.TerminalUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PipelineExecutor {

    public static void run(VshellApp shell, List<List<String>> stages) throws IOException, InterruptedException {
        TerminalUtil.resetTerminalMode();
        List<Process>  processes = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();
        InputStream prev = null;

        try {
            for (int i = 0; i < stages.size(); i++) {
                List<String> stage = stages.get(i);
                boolean first = i == 0;
                boolean last = i == stages.size() - 1;
                String name = stage.getFirst();
                List<String> params = stage.subList(1, stage.size());

                if (COMMANDS.fromString(name) == COMMANDS.EXTERNAL) {
                    ProcessBuilder pb = new ProcessBuilder(stage);
                    pb.redirectError(ProcessBuilder.Redirect.INHERIT);
                    pb.redirectInput(first ? ProcessBuilder.Redirect.INHERIT : ProcessBuilder.Redirect.PIPE);
                    pb.redirectOutput(last ? ProcessBuilder.Redirect.INHERIT : ProcessBuilder.Redirect.PIPE);
                    Process p = pb.start();
                    if (!first) {
                        threads.add(pump(prev, p.getOutputStream()));
                    }
                    prev = last ? null : p.getInputStream();
                    processes.add(p);
                } else {
                    if (prev != null) {
                        prev.close();
                        prev = null;
                    }
                    if (last) {
                        runBuiltin(shell, name, params);
                    } else {
                        PipedOutputStream pos = new PipedOutputStream();
                        PipedInputStream pis = new PipedInputStream(pos);
                        Thread t = new Thread(() -> {
                            try (PrintStream ps = new PrintStream(pos, true)) {
                                TerminalUtil.setOut(ps);
                                runBuiltin(shell, name, params);
                            } finally {
                                TerminalUtil.clearOut();
                            }
                        });
                        t.start();
                        threads.add(t);
                        prev = pis;
                    }
                }
            }
            boolean lastIsExternal = COMMANDS.fromString(stages.getLast().getFirst()) == COMMANDS.EXTERNAL;
            if (lastIsExternal) {
                processes.getLast().waitFor();
            }
            // the pipeline ends when its last stage ends; stop producers like `tail -f`
            for (Process p : processes) p.destroy();
            for (Process p : processes) p.waitFor();
            for (Thread t : threads) t.join();
        } finally {
            TerminalUtil.setTerminalRawMode();
        }
    }

    private static Thread pump(InputStream in, OutputStream out) {
        Thread t = new Thread(() -> {
            try (in; out) {
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) != -1) {
                    out.write(buf, 0, n);
                    out.flush();
                }
            } catch (IOException e) {
            }
        });
        t.start();
        return t;
    }

    private static void runBuiltin(VshellApp shell, String name, List<String> params) {
        switch (COMMANDS.fromString(name)) {
            case ECHO -> BultinCmdExecutor.executeEcho(params);
            case TYPE -> BultinCmdExecutor.executeType(params);
            case PWD -> TerminalUtil.println(shell.getCurrentWorkingDir().toString());
            case CD -> BultinCmdExecutor.executeCD(shell, params);
            default -> { }
        }
    }
}
