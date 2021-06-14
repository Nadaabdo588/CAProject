import java.util.*;

public class ALU {
	public int ADD(byte r1, byte r2) {
		return  r1 +  r2;

	}

	public int SUB(byte r1, byte r2) {
		return  r1 -  r2;
	}

	public int MUL(byte r1, byte r2) {
		return  r1 *  r2;
	}

	public boolean BEQZ(byte r1, byte I) {
		return  r1 == 0;

	}

	public int AND(byte r1, byte r2) {
		return  r1 &=  r2;
	}

	public int Or(byte r1, byte r2) {
		return  r1 |=  r2;
	}

	public short JR(byte r1, byte r2) {
		short temp =  r1;
		temp = (short) (temp << 8);
		return temp = (short) (temp |  r2);
	}

	public int SLC(byte r1, byte imm) {
		imm = (byte) (imm % 8);
		 r1 = (byte) ((byte) ( r1 << imm) | (byte) ( r1 >> (8 - imm)));
		return  r1;
	}

	public int SRC(byte r1, byte imm) {
		imm = (byte) (imm % 8);
		 r1 = (byte) ((byte) ( r1 >> imm) | (byte) ( r1 << (8 - imm)));
		return  r1;
	}

//	public void LB(byte r1, byte address) {
//		 r1 = (byte) memory[address];
//	}
//
//	public void SB(byte r1, byte address) {
//		memory[address] =  r1;
//	}

	public static void main(String[] args) {

	}
}
