import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ZipfDistribution {
	
	private String file = null;
	
	private String content = null;
	
	private List<Word> words = new ArrayList<Word>();
	
	private long total = 0;
	
	public ZipfDistribution(String file) {
		this.file = file;
		
		this.analysis();
	}

	private void analysis() {
		// TODO Auto-generated method stub
		
		try {
			readFile();
			
			String [] tokens = content.split("[^\\p{Alpha}]+");
			
			List<String> refined = new ArrayList<String>();
			for (String token : tokens) {
				if (token.trim().length() <= 1 && !token.equals("a") && !token.equals("i"))
					continue;
				
				refined.add(token.trim().toLowerCase());
			}
			
			total = refined.size();
			
			Map<String, Long> map = refined.stream().collect(Collectors.groupingBy(p -> p, Collectors.counting()));
			
			Map<?, ?> map2 = map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (a, b) -> a + ", " + b, TreeMap::new));
						
			
			map2.entrySet().stream().forEach(p -> {
				words.add(new Word((String) p.getValue(), (Long) p.getKey()));
			});
			
			Collections.sort(words);
			
		//	words.stream().forEach(p -> System.out.println(p.getWord() + " : " + p.getCount()) );
		//	System.out.println("SIZE: " + words.size());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void readFile() throws Exception {		
		content = new String(Files.readAllBytes(Paths.get(file)));
	}
	
	public List<Word> getWords() {
		return words;
	}

	public void setWords(List<Word> words) {
		this.words = words;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
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
		public int compareTo(Word o) {
			// TODO Auto-generated method stub
			return this.count.compareTo(o.count) * -1;
		}
		
		@Override
		public String toString() {
			return "Word [word=" + word + ", count=" + count + "]";
		}
	}

}
