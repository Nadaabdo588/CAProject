public class Main {
    short PC;
    byte statusRegister;
    short[] instructionMemory;
    byte[] dataMemory;
    byte[] registerFile;

    public Main() {
        instructionMemory = new short[1024];
        dataMemory = new byte[2048];
        registerFile = new byte[64];
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
        decodedInst[0] = (byte) (instruction % (1 << 4));
        return decodedInst;
    }

    public void updateCarry(int result) {
        statusRegister = (result > Byte.MAX_VALUE) ? (byte) (statusRegister | (1 << 4)) : (byte) (statusRegister & (~(1 << 4)));
    }

    public void updateOVF(int result, byte op1, byte op2) {
        int mul = op1 * op2;
        if (mul > 0 && ((byte) (result)) * op1 < 0) {
            statusRegister = (byte) (statusRegister | (1 << 3));
        } else {
            statusRegister = (byte) (statusRegister & (~(1 << 3)));
        }
    }

    public void updateNegative(int result) {
        statusRegister = (((byte) result) < 0) ? (byte) (statusRegister | (1 << 2)) : (byte) (statusRegister & (~(1 << 2)));
    }

    public void updateSignFlag() {
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
        switch (decodedInst[0]) {
            case 0:
                //add
                //update 5 flags
                break;
            case 1:
                //sub
                //update 5 flags
                break;
            case 2:
                //mul
                //update C,N,Z
                break;
            case 3:
                //load imm
                registerFile[decodedInst[1]] = decodedInst[2];
                break;
            case 4:
                //beqz
                break;
            case 5:
                //and
                //update N,Z
                break;
            case 6:
                //or
                //update N,Z
                break;
            case 7:
                //jump reg
                PC = (short) (registerFile[decodedInst[1]] << 8 + registerFile[decodedInst[2]]);
                break;
            case 8:
                //slc
                //update N,Z
                break;
            case 9:
                //src
                //update N,Z
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

    public static void main(String[] args) {

    }
}
