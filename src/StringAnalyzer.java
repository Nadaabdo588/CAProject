
import java.util.LinkedList;

public class StringAnalyzer {

	private LinkedList<String> words;
	private int wordIndex;

	public StringAnalyzer(String s) {
		words = new LinkedList<String>();
		boolean isChar = false;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length()&&s.charAt(i)!='#'; i++) {
			if (isChar && isDelimiter(s.charAt(i))) {
				words.add(sb.toString());
				isChar = false;
				sb = new StringBuilder();
			} else if (!isDelimiter(s.charAt(i))) {
				sb.append(s.charAt(i));
				isChar = true;
			} 
		}
		if (sb.length() != 0)
			words.add(sb.toString());
		wordIndex = 0;
	}

	private static boolean isDelimiter(char c) {
		return c == ' ';
	}

	public String nextWord() throws Exception {
		if (wordIndex == words.size())
			throw new Exception("invalid program");
		else
			return words.get(wordIndex++).toUpperCase();
	}

	public String readNextWord() throws Exception {
		if (wordIndex == words.size())
			throw new Exception("invalid program");
		else
			return words.get(wordIndex).toUpperCase();
	}

	public boolean hasMoreWords() {
		return !(wordIndex == words.size());
	}

	public static void main(String[] args){
		StringAnalyzer sa = new StringAnalyzer("ADD R1 R2 #i = i + 2");
		System.out.println(sa.words);
	}
}
