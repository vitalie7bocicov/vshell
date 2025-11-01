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
            String[] input = scanner.nextLine().split(" ");
            String inputCmd = input[0];
            List<String> params = new ArrayList<>(Arrays.asList(input).subList(1, input.length));
            switch (COMMANDS.fromString(inputCmd)) {
                case EXIT:  {
                    return;
                }
                case ECHO: {
                    System.out.println(String.join(" ", params));
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
