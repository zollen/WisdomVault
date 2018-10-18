import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ZipfExercise1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			String buf = readFile("data/USA.txt");
			
			String [] tokens = buf.split("[\\p{Space}\\p{Punct}\\p{Digit}]+");
			
			Map<String, Long> map = Arrays.stream(tokens).collect(Collectors.groupingBy(p -> p.toLowerCase(), Collectors.counting()));
			
			
			map.entrySet().stream().forEach(e -> {
				System.out.println("dept: [" + e.getKey() + "] ==> total: [" + e.getValue() + "]");
			});
			
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String readFile(String file) throws Exception {		
		return new String(Files.readAllBytes(Paths.get(file)));
	}

}
