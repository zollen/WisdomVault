import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ZipfExercise1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			String buf = readFile("data/USA.txt");
			
			String [] tokens = buf.split("[\\p{Space}\\p{Punct}\\p{Digit}]+");
			
			Map<String, Long> map = Arrays.stream(tokens).collect(Collectors.groupingBy(p -> p.toLowerCase(), Collectors.counting()));
						
			List<Word> words = new ArrayList<Word>();
			map.entrySet().stream().forEach(p -> {
				words.add(new Word(p.getKey(), p.getValue()));
			});
			
			Collections.sort(words);
			
			words.stream().forEach(p -> { System.out.println("[" + p.word + "] ===> " + p.count); });
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String readFile(String file) throws Exception {		
		return new String(Files.readAllBytes(Paths.get(file)));
	}
	
	public static class Word implements Comparable<Word>{
		
		private String word = null;
		private Long count = null;
		
		public Word(String word, Long count) {
			this.word = word;
			this.count = count;
		}
		
		public String getWord() {
			return word;
		}

		public void setWord(String word) {
			this.word = word;
		}

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
		}
		
		@Override
		public String toString() {
			return "Word [word=" + word + ", count=" + count + "]";
		}

		@Override
		public int compareTo(Word o) {
			// TODO Auto-generated method stub
			return this.count.compareTo(o.count) * -1;
		}
	}

}
