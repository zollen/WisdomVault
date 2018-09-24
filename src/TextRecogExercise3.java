import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class TextRecogExercise3 {


	public static void main(String[] args) throws Exception {

		int [][] data = loadData("data/car.data.txt");

		double [][][] classifier = learnClassifier(data);
		
		Record rec1 = new Record("low", "low", "4", "4", "big", "high");
		
		System.out.println(rec1);
		System.out.println("CONCLUSION: " + applyClassifier(classifier, rec1));
		
	}
	
	private static int applyClassifier(double [][][] classifier, Record record) {
		
		int [] inputs = record.toArray();
		
		double max = -999;
		int selected = -1;
		
		for (int i = 0; i < Record.NUM_OF_CLASS; i++) {
			
			double val = Math.log1p(classifier[i][Record.NUM_OF_ATTRS - 1][0]);
			
			for (int j = 0; j < Record.NUM_OF_ATTRS - 1; j++) {		
				val += Math.log1p(classifier[i][j][inputs[j]]);
			}
					
			if (val > max) {
				max = val;
				selected = i;
			}
		}
		
		return selected;
	}
	
	private static double[][][] learnClassifier(int [][] data) {
		
		double [][][] classifier = 
				new double[Record.NUM_OF_CLASS][Record.NUM_OF_ATTRS][Record.MAX_POSSIBLE_VALUES]; 
		
		for (int i = 0; i < Record.NUM_OF_CLASS; i++) {		
			for (int j = 0; j < Record.NUM_OF_ATTRS - 1; j++) {			
				for (int k = 0; k < Record.MAX_POSSIBLE_VALUES; k++) {
					int nominator = count(j, k, Record.CONCLUSION, i, data);
					int denominator = count(Record.CONCLUSION, i, data);
			//		System.out.println(i + ", " + j + ", " + k + " ==> " + nominator + " / " + denominator);
					if (nominator == 0 || denominator == 0)
						classifier[i][j][k] = 0;
					else
						classifier[i][j][k] = (double) nominator / denominator;
				}
			}
		}
		
		for (int i = 0; i < Record.NUM_OF_CLASS; i++) {
			int nominator = count(Record.CONCLUSION, i, data);
			int denominator = data.length;
			if (nominator == 0 || denominator == 0)
				classifier[i][Record.NUM_OF_ATTRS - 1][0] = 0;
			else
				classifier[i][Record.NUM_OF_ATTRS - 1][0] = (double) nominator / denominator;		
		}
			
		return classifier;	
	}

	private static int[][] loadData(String file) {

		CSVParser csvParser = null;
		List<Record> records = new ArrayList<Record>();

		try {
			Reader reader = Files.newBufferedReader(Paths.get(file));
			csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withIgnoreHeaderCase().
					withIgnoreEmptyLines().withTrim());

			for (CSVRecord csvRecord : csvParser) {
				Record rec = new Record(csvRecord);
				records.add(rec);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (csvParser != null)
					csvParser.close();
			} catch (Exception e) {
			}
		}

		int [][] data = new int[records.size()][Record.NUM_OF_ATTRS];
		
		int index = 0;
		for (Record record : records) {
			
			int [] arr = record.toArray();
			for (int i = 0; i < Record.NUM_OF_ATTRS; i++)
				data[index][i] = arr[i];	
			index++;
		}

		return data;
	}

	private static class Record {
		
		public static final int CONCLUSION = 6;
		
		public static final int MAX_POSSIBLE_VALUES = 4;
		
		public static final String [] val1 = { "vhigh", "high", "med", "low" };
		public static final String [] val2 = { "2", "3", "4", "5more" };
		public static final String [] val3 = { "2", "4", "more" };
		public static final String [] val4 = { "small", "med", "big" };
		public static final String [] val5 = { "low", "med", "high" };
		public static final String [] val6 = { "unacc", "acc", "good", "vgood" };
		
		public static final int NUM_OF_ATTRS = 7;
		public static final int NUM_OF_CLASS = 4;
		
		
		private long index = -1;
		private String buying = null;
		private String maint = null;
		private String doors = null;
		private String persons = null;
		private String lug = null;
		private String safety = null;
		private String conclusion = null;
		
		public Record(CSVRecord record) {
			this.index = record.getRecordNumber();
			this.buying = record.get(0);
			this.maint = record.get(1);
			this.doors = record.get(2);
			this.persons = record.get(3);
			this.lug = record.get(4);
			this.safety = record.get(5);
			this.conclusion = record.get(6);
		}
		
		public Record(String buying, String maint, String doors, String persons, String lug, String safety) {
			super();
			this.buying = buying;
			this.maint = maint;
			this.doors = doors;
			this.persons = persons;
			this.lug = lug;
			this.safety = safety;
		}
		
		public String getBuying() {
			return buying;
		}

		public String getMaint() {
			return maint;
		}

		public String getDoors() {
			return doors;
		}

		public String getPersons() {
			return persons;
		}

		public String getLug() {
			return lug;
		}

		public String getSafety() {
			return safety;
		}

		public String getConclusion() {
			return conclusion;
		}

		public static int format(String val, String [] possibleValues) {
			
			int index = 0;
			for (String value : possibleValues) {
				
				if (value.equalsIgnoreCase(val))
					return index;
				
				index++;
			}
			
			return -9999;
			
		}
		
		@Override
		public String toString() {
			return "Record [index=" + index + ", buying=" + buying + ", maint=" + maint + ", doors=" + doors
					+ ", persons=" + persons + ", lug=" + lug + ", safety=" + safety + ", conclusion=" + conclusion
					+ "]";
		}
		
		public int [] toArray() {
			
			int [] arr = new int[NUM_OF_ATTRS];
			arr[0] = Record.format(this.getBuying(), Record.val1);
			arr[1] = Record.format(this.getMaint(), Record.val1);
			arr[2] = Record.format(this.getDoors(), Record.val2);
			arr[3] = Record.format(this.getPersons(), Record.val3);
			arr[4] = Record.format(this.getLug(), Record.val4);
			arr[5] = Record.format(this.getSafety(), Record.val5);
			arr[6] = Record.format(this.getConclusion(), Record.val6);
			
			return arr;
		}
	}
	


	public static int count(int col, int val, int [][] A) {

		int count = 0;
		for (int i = 0; i < A.length; i++)
			if (A[i][col] == val)
				count++;

		return count;
	}

	public static int count(int col1, int val1, int col2, int val2, int [][] A) {

		int count = 0;

		for (int i = 0; i < A.length; i++)
			if (A[i][col1] == val1 && A[i][col2] == val2)
				count++;

		return count;
	}

}
