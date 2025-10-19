import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("$ ");
            String[] input = scanner.nextLine().split(" ");
            String cmd = input[0];
            String param = input.length > 1 ? input[1] : "";

            if (cmd.equals("exit")) {
                return;
            }

            System.out.println(input[0] + ": command not found");
        }
    }
}
