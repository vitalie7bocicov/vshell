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
                        if (COMMANDS.isValid(param)) {
                            System.out.println(param + " is a shell builtin");
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
}
