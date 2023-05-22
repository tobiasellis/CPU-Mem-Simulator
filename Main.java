import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Main {

    public static void main(String[] args) {
        try {
            Process memory = Runtime.getRuntime().exec("java Memory " + args[0]);
            int instructionsLimit = Integer.parseInt(args[1]);
            InputStream inPipe = memory.getInputStream();
            OutputStream outPipe = memory.getOutputStream();

            CPU cpu = new CPU(inPipe, outPipe, instructionsLimit);
            System.out.println("Starting CPU...");
            cpu.startCPU();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
