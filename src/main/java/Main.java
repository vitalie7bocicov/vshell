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
            String cmd = input[0];
            List<String> params = new ArrayList<>(Arrays.asList(input).subList(1, input.length));

            switch (cmd) {
                case "exit":  {
                    return;
                }
                case "echo": {
                    System.out.println(String.join(" ", params));
                    break;
                }
                default: {
                    System.out.println(input[0] + ": command not found");
                }
            }
        }
    }
}
