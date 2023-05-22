import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class CPU {
    // Registers
    private static int pc = 0;
    private static int sp = 1000;
    private static int ir = 0;
    private static int ac = 0;
    private static int x = 0;
    private static int y = 0;

    // Timer
    private static int timer = 0;
    private static int instructionsLimit = 0;

    // Flags
    private static boolean procInterupt = false;
    private static boolean kernalMode = false;
    private static boolean debugMode = false;

    // Datastreams
    private static Scanner input;
    private static PrintWriter output;

    // Construcer for CPU. Includes Input and Output streams linked to Memory, and
    // timer limit from args.
    public CPU(InputStream in, OutputStream out, int instructionsLimit) {
        CPU.input = new Scanner(in);
        CPU.output = new PrintWriter(out);
        CPU.instructionsLimit = instructionsLimit;
    }

    // Writes a request to read an address from memory if it has permission
    public int readMemory(int address) {
        String response;
        if (kernalMode || (!kernalMode && address < 1000)) {
            output.println("READ:" + address);
            output.flush();

            response = input.next();
            return Integer.parseInt(response);
        } else {
            System.out.println("Memory violation: accessing system address " + address + " in user mode");
            System.exit(0);
        }
        return -1;
    }

    // Writes a request to write data to an address from memory if it has permission
    public void writeMemory(int data, int address) {
        if (kernalMode || (!kernalMode && address < 1000)) {
            output.println("WRITE:" + address + ":" + data);
            output.flush();
        } else {
            System.out.println("ERROR!!! CANNOT WRITE TO MEMORY, TRY KERNAL MODE");
            System.exit(0);
        }
    }

    // First decrements stack pointer to avoid writing to 1000, than uses
    // writeMemory function to process write request
    public void pushStack(int data) {
        sp--;
        writeMemory(data, sp);
    }

    // Uses readMemory function to process read request, then increments stack.
    public int popStack() {
        int popped = readMemory(sp);
        sp++;
        return popped;
    }

    // Fetches next instruction from memory, and increments program counter
    public void fetchInstruction() {
        ir = readMemory(pc);
        pc++;
    }

    // Processes fetched instruction stored in IR
    public void processInstruction() {
        switch (ir) {
            case 1: // 1 = Load value
                fetchInstruction();
                ac = ir;
                break;
            case 2: // 2 = Load addr
                fetchInstruction();
                ac = readMemory(ir);
                break;
            case 3: // 3 = LoadInd addr
                fetchInstruction();
                ac = readMemory(readMemory(ir));
                break;
            case 4: // 4 = LoadIdxX addr
                fetchInstruction();
                ac = readMemory(ir + x);
                break;
            case 5: // 5 = LoadIdxY addr
                fetchInstruction();
                ac = readMemory(ir + y);
                break;
            case 6: // 6 = LoadSpX
                ac = readMemory(sp + x);
                break;
            case 7: // 7 = Store addr
                fetchInstruction();
                writeMemory(ac, ir);
                break;
            case 8: // 8 = Get
                ac = (int) (Math.random() * 100 + 1);
                break;
            case 9: // 9 = Put port
                fetchInstruction();

                if (ir == 1) { // Print data as int
                    System.out.print(ac);
                } else if (ir == 2) { // print data as char
                    System.out.print((char) ac);
                }
                break;
            case 10: // 10 = AddX
                ac = ac + x;
                break;
            case 11: // 11 = AddY
                ac = ac + y;
                break;
            case 12: // 12 = SubX
                ac = ac - x;
                break;
            case 13: // 13 = SubY
                ac = ac - y;
                break;
            case 14: // 14 = CopyToX
                x = ac;
                break;
            case 15: // 15 = CopyFromX
                ac = x;
                break;
            case 16: // 16 = CopyToY
                y = ac;
                break;
            case 17: // 17 = CopyFromY
                ac = y;
                break;
            case 18: // 18 = CopyToSp
                sp = ac;
                break;
            case 19: // 19 = CopyFromSp
                ac = sp;
                break;
            case 20: // 20 = Jump addr
                fetchInstruction();
                pc = ir;
                break;
            case 21: // 21 = JumpIfEqual addr
                fetchInstruction();
                if (ac == 0)
                    pc = ir;
                break;
            case 22: // 22 = JumpIfNotEqual addr
                fetchInstruction();
                if (ac != 0)
                    pc = ir;
                break;
            case 23: // 23 = Call addr
                fetchInstruction();
                pushStack(pc);
                pc = ir;
                break;
            case 24: // 24 = Ret
                pc = popStack();
                break;
            case 25: // 25 = IncX
                x++;
                break;
            case 26: // 26 = DecX
                x--;
                break;
            case 27: // 27 = Push
                pushStack(ac);
                break;
            case 28: // 28 = Pop
                ac = popStack();
                break;
            case 29: // 29 = Int
                if (!procInterupt) {
                    procInterupt = true;
                    kernalMode = true; // CPU should enter kernal mode

                    writeMemory(sp, 1999); // Save SP to system stack
                    writeMemory(pc, 1998); // Save pc to system stack

                    pc = 1500; // Int executes at 1500
                    sp = 1998;
                }
                break;
            case 30: // 30 = IRet
                pc = popStack();
                sp = popStack();

                kernalMode = false;
                procInterupt = false;
                break;
            case 50: // 50 = End
                System.exit(0);
                break;
            default:
                System.out.println("instruction not recognized: " + ir);
                break;

        }
    }

    // For debugging purposes
    public void debug_print() {
        System.out.println("\nPC: " + pc + ", AC: " + ac + ", IR: " + ir + ", SP: " + sp + ", X: " + x + ", Y: " + y);
    }

    // Called by Main, runs CPU and starts reading instructions. Allows us to hold
    // off processing until Memory is loaded
    public void startCPU() {
        while (true) {
            if (debugMode)
                debug_print();
            if (timer % instructionsLimit != 0 || timer <= 0) {
                fetchInstruction();
                processInstruction();

            } else {
                if (!procInterupt) {
                    procInterupt = true;
                    kernalMode = true; // CPU should enter kernal mode

                    writeMemory(sp, 1999); // Save SP to system stack
                    writeMemory(pc, 1998); // Save pc to system stack

                    pc = 1000; // PC 1000 for timer
                    sp = 1998;
                }
            }
            timer++;
        }
    }

}
