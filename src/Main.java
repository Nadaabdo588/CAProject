public class Main {
    short PC;
    byte statusRegister;
    short[] instructionMemory;
    byte[] dataMemory;
    byte[] registerFile;
    ALU alu;

    public Main() {
        instructionMemory = new short[1024];
        dataMemory = new byte[2048];
        registerFile = new byte[64];
        alu = new ALU();
//        PC=0;
//        statusRegister=0;
    }

    public short fetch() {
        return instructionMemory[PC];
    }

    public byte[] decode(short instruction) {
        PC++;
        byte[] decodedInst = new byte[3];
        decodedInst[2] = (byte) (instruction % (1 << 6));
        instruction >>= 6;
        decodedInst[1] = (byte) (instruction % (1 << 6));
        instruction >>= 6;
        decodedInst[0] = (byte) (instruction);
        return decodedInst;
    }

    public void updateCarry(int result) {

        statusRegister = ((result & (1<<8))!=0) ? (byte) (statusRegister | (1 << 4)) : (byte) (statusRegister & (~(1 << 4)));
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
        if ((nFlag != 0 && ovfFlag != 0) || (nFlag == 0 && ovfFlag == 0))
        {
            statusRegister = (byte) (statusRegister & (~(1 << 1)));
        }else{
            statusRegister = (byte) (statusRegister | (1 << 1));

        }
    }

    public void updateZero(int result) {
        statusRegister = (((byte) result) == 0) ? (byte) (statusRegister | 1) : (byte) (statusRegister & (~(1)));
    }


    public void execute(byte[] decodedInst) {
        byte op1=registerFile[decodedInst[1]], op2 = registerFile[decodedInst[2]];
        int result = 0;
        switch (decodedInst[0]) {
            case 0:
                // add
                result = alu.ADD(op1, op2);
                registerFile[decodedInst[1]] = (byte) result;
                //update 5 flags
                updateCarry(result);
                updateOVF(result, op1,op2);
                updateNegative(result);
                updateSign();
                updateZero(result);
                break;
            case 1:
                // subtract
                result = alu.SUB(op1, op2);
                registerFile[decodedInst[1]] = (byte) result;
                //update 5 flags
                updateCarry(result);
                updateOVF(result, op1,op2);
                updateNegative(result);
                updateSign();
                updateZero(result);
                break;
            case 2:
                // multiply
                result = alu.MUL(op1, op2);
                registerFile[decodedInst[1]] = (byte) result;
                //update C,N,Z
                updateCarry(result);
                updateNegative(result);
                updateZero(result);
                break;
            case 3:
                //load imm
                registerFile[decodedInst[1]] = decodedInst[2];
                break;
            case 4:
                //branch if equal to zero
                if(alu.BEQZ(op1))
                    PC = (short)(PC+ decodedInst[2]);
                break;
            case 5:
                // and
                result = alu.AND(op1, op2);
                registerFile[decodedInst[1]] = (byte) result;
                //update N,Z
                updateNegative(result);
                updateZero(result);
                break;
            case 6:
                // or
                result = alu.Or(op1 , op2);
                registerFile[decodedInst[1]] = (byte) result;
                //update N,Z
                updateNegative(result);
                updateZero(result);
                break;
            case 7:
                //jump reg
                PC = alu.JR(op1, op2);
                break;
            case 8:
                //shift left circular
                result = alu.SLC(op1 , op2);
                registerFile[decodedInst[1]] = (byte) result;
                //update N,Z
                updateNegative(result);
                updateZero(result);
                break;
            case 9:
                // shift right circular
                result = alu.SRC(op1, op2);
                registerFile[decodedInst[1]] = (byte) result;
                //update N,Z
                updateNegative(result);
                updateZero(result);
                break;
            case 10:
                //load byte
                registerFile[decodedInst[1]] = dataMemory[decodedInst[2]];
                break;
            case 11:
                //store byte
                dataMemory[decodedInst[2]] = registerFile[decodedInst[1]];
                break;
            default:
                System.out.println("Invalid instruction");


        }
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


    }

    public static void main(String[] args) throws Exception {
//        Main cpu = new Main();
//        Assembler assembler = new Assembler(cpu.instructionMemory);
//        int maxIndex = assembler.assemble("program 1");
        byte x=-64;
        byte y=8;
        x+=y;
        System.out.println(32);
        for(int i=0;i<32;i++)
        System.out.println(((x*y)&(1<<i)));

    }
}
