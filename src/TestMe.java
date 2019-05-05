import java.io.File;

import com.google.common.io.Files;


public class TestMe {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String base = "C:\\Users\\zollen\\Downloads\\organizer";
		String target = "C:\\Users\\zollen\\eclipse-workspace\\WisdomVault\\img\\digits";
		
		for (int i = 0, index = 1000; i < 10; i++) {
			
			File dir = new File(base + File.separator + i);
			
			File [] files = dir.listFiles();
			
			for (File file : files) {
				if (file.getName().endsWith(".png")) {
				
					Files.copy(file, 
							new File(target + File.separator + i + "_" + index + "_TRAINING.png"));
					index++;
				}
			}
		}
	}
}
