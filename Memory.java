import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Memory {
    public static int[] memory = new int[2000];
    public static boolean debugMode = false;

    public static void main(String[] args) {
        try {
            initMemory(args[0]);
        } catch (FileNotFoundException e) {
            System.exit(0);
        }

        if (debugMode)
            debug_print(); // For Debugging

        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            String command = line.split(":")[0];
            String address = line.split(":")[1];

            if (command.equals("READ")) { // process Read requests
                int a = Integer.parseInt(address);
                if (a < 2000) {
                    System.out.println(memory[a]);
                }
            } else if (command.equals("WRITE")) { // process write requests
                String data = line.split(":")[2];

                int a = Integer.parseInt(address);
                int d = Integer.parseInt(data);

                if (a < 2000) { // Check out of bound before writing
                    memory[a] = d;
                }

            } else {
                // Command not recognized
            }
        }

        System.exit(0);
    }

    public static void debug_print() {
        for (int i = 0; i < 2000; i++) {
            if (memory[i] != 0) {
                System.out.println("A: " + i + "\tD: " + memory[i]);
            }
        }
    }

    public static void initMemory(String fileName) throws FileNotFoundException {
        int address = 0;
        String line = "";
        File file = new File(fileName);
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {

            // Gets data before text
            line = scanner.nextLine().split(" ")[0];
            if (line.trim().isEmpty()) {
                // Line is empty, therefore skip
            } else if (line.charAt(0) == '.') {
                // Set address to integer after "."
                address = Integer.parseInt(line.substring(1, line.length()));
            } else {
                // Set mem[address] = data
                memory[address] = Integer.parseInt(line);
                address++;
            }

        }

    }
}