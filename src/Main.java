import java.util.Arrays;
import java.util.Stack;

public class Main {
    short PC;
    byte statusRegister;
    short[] instructionMemory;
    byte[] dataMemory;
    byte[] registerFile;
    ALU alu;
    short decodedPC;
    short executedPC;


    public Main() {
        instructionMemory = new short[1024];
        dataMemory = new byte[2048];
        registerFile = new byte[64];
        alu = new ALU();
        PC = 0;
        statusRegister = 0;
        decodedPC = -1;
        executedPC = -1;
    }

    public short fetch() {
        return instructionMemory[PC];
    }

    public byte[] decode(short instruction) {
//        executedPC=decodedPC;
//        decodedPC=PC;
        PC++;
        int ay=(1<<6)-1;
        int az=(1<<4)-1;
        byte[] decodedInst = new byte[3];
        decodedInst[2] = (byte) (instruction &ay);
        ay=(1<<10)-1;
        instruction >>= 6;
        instruction&=ay;
        ay=(1<<6)-1;
        decodedInst[1] = (byte) (instruction &ay);
        instruction >>= 6;
        instruction&=az;
        decodedInst[0] = (byte) (instruction);
        return decodedInst;
    }

    public void updateCarry(int result) {

        statusRegister = ((result & (1 << 8)) != 0) ? (byte) (statusRegister | (1 << 4)) : (byte) (statusRegister & (~(1 << 4)));
    }

    public void updateOVF(int result, byte op1, byte op2) {
        // apply masking on operands and results
        int mul = alu.MUL(op1, op2);
        if (mul > 0 && ((byte) (result)) * op1 < 0) {
            statusRegister = (byte) (statusRegister | (1 << 3));
        } else {
            statusRegister = (byte) (statusRegister & (~(1 << 3)));
        }
    }

    public void updateNegative(int result) {
        statusRegister = (((byte) result) < 0) ? (byte) (statusRegister | (1 << 2)) : (byte) (statusRegister & (~(1 << 2)));
    }

    public void updateSign() {
        int nFlag = statusRegister & (1 << 2);
        int ovfFlag = statusRegister & (1 << 3);
        if ((nFlag != 0 && ovfFlag != 0) || (nFlag == 0 && ovfFlag == 0)) {
            statusRegister = (byte) (statusRegister & (~(1 << 1)));
        } else {
            statusRegister = (byte) (statusRegister | (1 << 1));

        }
    }

    public void updateZero(int result) {
        statusRegister = (((byte) result) == 0) ? (byte) (statusRegister | 1) : (byte) (statusRegister & (~(1)));
    }

    public boolean execute(byte[] decodedInst) {
        byte op1 = registerFile[decodedInst[1]], op2 = registerFile[decodedInst[2]];
        int result = 0;
        byte saveStatus=statusRegister;
        switch (decodedInst[0]) {
            case 0:
                // add
                result = alu.ADD(op1, op2);
                registerFile[decodedInst[1]] = (byte) result;
                System.out.println("Updated register -> R"+decodedInst[1]+": "+registerFile[decodedInst[1]]);
                //update 5 flags
                updateCarry(result);
                updateOVF(result, op1, op2);
                updateNegative(result);
                updateSign();
                updateZero(result);
                break;
            case 1:
                // subtract
                result = alu.SUB(op1, op2);
                registerFile[decodedInst[1]] = (byte) result;
                System.out.println("Updated register -> R"+decodedInst[1]+": "+registerFile[decodedInst[1]]);

                //update 5 flags
                updateCarry(result);
                updateOVF(result, op1, op2);
                updateNegative(result);
                updateSign();
                updateZero(result);
                break;
            case 2:
                // multiply
                result = alu.MUL(op1, op2);
                registerFile[decodedInst[1]] = (byte) result;
                System.out.println("Updated register -> R"+decodedInst[1]+": "+registerFile[decodedInst[1]]);

                //update C,N,Z
                updateCarry(result);
                updateNegative(result);
                updateZero(result);
                break;
            case 3:
                //load imm
                registerFile[decodedInst[1]] = decodedInst[2];
                System.out.println("Updated register -> R"+decodedInst[1]+": "+registerFile[decodedInst[1]]);

                break;
            case 4:
                //branch if equal to zero
                if (alu.BEQZ(op1)) {
                    PC = (short) (PC + decodedInst[2]);
                    return true;
                }
                break;
            case 5:
                // and
                result = alu.AND(op1, op2);
                registerFile[decodedInst[1]] = (byte) result;
                System.out.println("Updated register -> R"+decodedInst[1]+": "+registerFile[decodedInst[1]]);

                //update N,Z
                updateNegative(result);
                updateZero(result);
                break;
            case 6:
                // or
                result = alu.Or(op1, op2);
                registerFile[decodedInst[1]] = (byte) result;
                System.out.println("Updated register -> R"+decodedInst[1]+": "+registerFile[decodedInst[1]]);

                //update N,Z
                updateNegative(result);
                updateZero(result);
                break;
            case 7:
                //jump reg
                PC = alu.JR(op1, op2);
                return true;
            case 8:
                //shift left circular
                result = alu.SLC(op1, op2);
                registerFile[decodedInst[1]] = (byte) result;
                System.out.println("Updated register -> R"+decodedInst[1]+": "+registerFile[decodedInst[1]]);

                //update N,Z
                updateNegative(result);
                updateZero(result);
                break;
            case 9:
                // shift right circular
                result = alu.SRC(op1, op2);
                registerFile[decodedInst[1]] = (byte) result;
                System.out.println("Updated register -> R"+decodedInst[1]+": "+registerFile[decodedInst[1]]);

                //update N,Z
                updateNegative(result);
                updateZero(result);
                break;
            case 10:
                //load byte
                registerFile[decodedInst[1]] = dataMemory[decodedInst[2]];
                System.out.println("Updated register -> R"+decodedInst[1]+": "+registerFile[decodedInst[1]]);
                break;
            case 11:
                //store byte
                dataMemory[decodedInst[2]] = registerFile[decodedInst[1]];
                System.out.println("Updated  memory address: "+decodedInst[2]+" New memory value: "+dataMemory[decodedInst[2]]);
                break;
            default:
                System.out.println("Invalid instruction");


        }
        if(saveStatus!=statusRegister)
            System.out.println("Status register: "+Assembler.valToBin(statusRegister,5));
        return false;
    }

    public void executeProgram(String fileName) throws Exception {
        PC = 0;
        statusRegister = 0;
        instructionMemory = new short[1024];
        dataMemory = new byte[2048];
        registerFile = new byte[64];
        Assembler assembler = new Assembler(instructionMemory);
        int maxIndex = assembler.assemble(fileName);
        int clkCycles = 0;
        short toBeDecoded = Short.MIN_VALUE;
        byte[] toBeExecuted = null;
        while (PC <= maxIndex||toBeDecoded!=Short.MIN_VALUE||toBeExecuted!=null) {
            System.out.println("Cycle: " + (++clkCycles));
            Stack<String> s = new Stack<>();
            boolean branched = false;
            if (toBeExecuted != null) {
                System.out.println("Currently executed instruction -> " + "PC: " + executedPC + " Instruction: " +
                        Assembler.valToBin(toBeExecuted[0],4) + " " + Assembler.valToBin(toBeExecuted[1],6) +
                        " " + Assembler.valToBin(toBeExecuted[2],6));
                branched = execute(toBeExecuted);
                toBeExecuted=null;
                executedPC=-1;
                if (branched) {
                    toBeDecoded = Short.MIN_VALUE;
                    toBeExecuted = null;
                    executedPC = -1;
                    decodedPC = -1;

                }
            }
            if (toBeDecoded != Short.MIN_VALUE && !branched) {
                System.out.println("Currently decoded instruction -> " + "PC: " + decodedPC + " Instruction: " + Assembler.valToBin(toBeDecoded,decodedPC));
                toBeExecuted = decode(toBeDecoded);
                executedPC = decodedPC;
                toBeDecoded=Short.MIN_VALUE;
                decodedPC=-1;
            }
            if (PC <= maxIndex && !branched) {
                System.out.println("Currently fetched instruction -> " + "PC:" + PC);
                toBeDecoded = fetch();
                decodedPC = PC;
            }

            System.out.println("------------------------------------------------------------------------------");
        }
        System.out.println("Register File");
        for(int i=0;i<registerFile.length;i++)
        {
            System.out.println("R"+(i)+":  "+Assembler.valToBin(registerFile[i],8));
        }
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Instruction Memory");

        for(int i=0;i<instructionMemory.length;i++)
        {
            System.out.println("Instruction memory address: "+i+" Instruction: "+Assembler.valToBin(instructionMemory[i],16));
        }
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("Data Memory");
        for (int i=0;i<dataMemory.length;i++)
        {
            System.out.println("Data memory address: "+i+" Data: "+Assembler.valToBin(dataMemory[i],8));
        }



    }

    public static void main(String[] args) throws Exception {
        Main cpu = new Main();
        cpu.executeProgram("program1");


    }
}
