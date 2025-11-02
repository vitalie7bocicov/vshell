import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class VshellApp {

    Path currentWorkingDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
    
    public void process() throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine();
            String[] args = input.split("\\s+");
            String inputCmd = args[0];
            List<String> params = new ArrayList<>(Arrays.asList(args).subList(1, args.length));
            switch (COMMANDS.fromString(inputCmd)) {
                case EXIT:  {
                    return;
                }
                case ECHO: {
                    String param = input.substring(5);
                    CommandsUtil.executeEcho(param);
                    break;
                }
                case TYPE: {
                    CommandsUtil.executeTypeCmd(params);
                    break;
                }
                case PWD: {
                    System.out.println(this.currentWorkingDir);
                    break;
                }
                case CD: {
                    CommandsUtil.executeCD(this, params);
                    break;
                }
                case EXTERNAL: {
                    CommandsUtil.runExternalProgram(inputCmd, params);
                    break;
                }
                default: {
                    break;
                }
            }
        }
        
    }
}
