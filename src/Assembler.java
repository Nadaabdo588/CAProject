import java.io.BufferedReader;
import java.io.FileReader;

public class Assembler {

	private short[] instMem;

	public Assembler(short[] instMem) {
		this.instMem = instMem;
	}

	public int assemble(String fileName) throws Exception {
		String path = "src/" + fileName + ".txt";
		BufferedReader br = new BufferedReader(new FileReader(path));
		String nextLine;
		int pc = 0;
		while ((nextLine = br.readLine()) != null) {
			StringAnalyzer sa = new StringAnalyzer(nextLine);
			Short nextInst = getInst(sa);
			if (pc == instMem.length)
				throw new Exception("Your large program can't fit in memory");
			instMem[pc++] = nextInst;
		}
//		if (pc == instMem.length)
//			throw new Exception("Your large program can't fit in memory");
//		String halt_inst = "1100000000000000";
//		instMem[pc++] = (short) Integer.parseInt(halt_inst, 2);
		return pc-1;
	}

	private static short getInst(StringAnalyzer sa) throws Exception {
		String inst = "";
		boolean immediate = false;
		switch (sa.nextWord()) {
		case "ADD":
			inst += "0000";
			break;
		case "SUB":
			inst += "0001";
			break;
		case "MUL":
			inst += "0010";
			break;
		case "LDI":
			inst += "0011";
			immediate = true;
			break;
		case "BEQZ":
			inst += "0100";
			immediate = true;
			break;
		case "AND":
			inst += "0101";
			break;
		case "OR":
			inst += "0110";
			break;
		case "JR":
			inst += "0111";
			break;
		case "SLC":
			inst += "1000";
			immediate = true;
			break;
		case "SRC":
			inst += "1001";
			immediate = true;
			break;
		case "LB":
			inst += "1010";
			immediate = true;
			break;
		case "SB":
			inst += "1011";
			immediate = true;
			break;
		default:
			throw new Exception("undefined instruction");
		}
		if (sa.readNextWord().charAt(0) != 'R')
			throw new Exception("expected register after instruction");
		short r1 = Short.parseShort(sa.nextWord().substring(1));
		if (r1 < 0 || r1 > 63)
			throw new Exception("Register out of bound, range is 0-63");
		String r1_bin = valToBin(r1, 6);
		inst += r1_bin;
		short r2 = 0;
		if (sa.readNextWord().charAt(0) == 'R') {
			if(immediate)
				throw new Exception("expected immediate value not register");
			r2 = Short.parseShort(sa.nextWord().substring(1));
			if (r2 < 0 || r2 > 63)
				throw new Exception("Register out of bound, range is 0-63");
		} else {
			if(!immediate)
				throw new Exception("expected register not immediate value");
			r2 = Short.parseShort(sa.nextWord());
			if (r2 < 0 || r2 > 63)
				throw new Exception("Immediate out of bound, range is 0-63");
		}
		String r2_bin = valToBin(r2, 6);
		inst += r2_bin;
		return (short) Integer.parseInt(inst, 2);
	}
	
	private static String valToBin(short x, int size) {
		String tmp = Integer.toBinaryString(0xFFFF & x);
		while(tmp.length()<size)
			tmp = "0"+tmp;
		return tmp;
	}
	
	public static void main(String[] args) throws Exception {
		Assembler as = new Assembler(new short[10]);
		as.assemble("program1");
		for(int i = 0; i < 10; i++)
			System.out.println(valToBin(as.instMem[i],16));
	}
}
