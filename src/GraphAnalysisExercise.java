import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GraphAnalysisExercise {
	
	// A <-> B <-> D
	// A <-> C <-> D
	// B <-> C
	
	// A : 0
	// B : 1
	// C : 2
	// D : 3
	
	// AB, BA : 01, 10 : 1
	// AC, CA : 02, 20 : 3
	// BD, DB : 13, 31 : 5
	// CD, DC : 23, 32 : 7
	// BC, CB : 12, 21 : 9
	
	private static final int [][] PATHS = { 
			{ 1, 2 },
			{ 0, 2, 3 },
			{ 0, 1, 3 },
			{ 1, 2 }	
	};
	
	private static final Map<Integer, List<Set<Integer>>> map = new HashMap<Integer, List<Set<Integer>>>();
	
	private static final Map<Integer, Integer> WEIGHTS = new HashMap<Integer, Integer>();
	
	static {	
		WEIGHTS.put(01, 1);
		WEIGHTS.put(10, 1);
		WEIGHTS.put(02, 7);
		WEIGHTS.put(20, 7);
		WEIGHTS.put(13, 17);
		WEIGHTS.put(31, 17);
		WEIGHTS.put(23, 67);
		WEIGHTS.put(32, 67);
		WEIGHTS.put(12, 97);
		WEIGHTS.put(21, 97);
		
		map.put(0, new ArrayList<Set<Integer>>());
		map.put(1, new ArrayList<Set<Integer>>());
		map.put(2, new ArrayList<Set<Integer>>());
		map.put(3, new ArrayList<Set<Integer>>());
	}
	
	private static final Set<Integer> weights = new HashSet<Integer>();
	
	private static final Set<Set<Integer>> result = new HashSet<Set<Integer>>();
	
	private static final int DEPTH = 6;
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		for (int i = 0; i < 4; i++)
			traverseGraph(i, DEPTH);
		
		normalizeGraph(DEPTH);
		
		printGraph();
	}
	

	public static void traverseGraph(int start, int depth) {
		
		for (int child : PATHS[start]) {
			
			Set<Integer> paths = new HashSet<Integer>();
			
			map.get(start).add(paths);
				
			paths.add(start * 10 + child);
			
			traverseGraph(start, child, depth - 1, paths);
		}
	}
	
	public static void traverseGraph(int start, int current, int depth, Set<Integer> set) {
		
		if (depth <= 0) {
			return;
		}
		
		for (int child : PATHS[current]) {
			
			Set<Integer> paths = new HashSet<Integer>(set);
			
			map.get(start).add(paths);
			
			paths.add(current * 10 + child);
			
			traverseGraph(start, child, depth - 1, paths);
		}
	}
	
	public static void normalizeGraph(int depth) {
	
		for (Integer start : map.keySet()) {
			
			List<Set<Integer>> list = map.get(start);
			
			Set<Set<Integer>> nlist = new HashSet<Set<Integer>>();
			for (Set<Integer> set : list) {
			
				if (set.size() == depth && validSet(set)) {					
					nlist.add(set);
					result.add(set);
				}
			}
			
			map.get(start).clear();
			map.get(start).addAll(nlist);			
		}
	}
	
	public static void printGraph() {
		
		for (Set<Integer> set : result) {		
			System.out.println(toString(set));
		}
	}
	
	public static int calculateWeight(Set<Integer> set) {
		
		int count = 0;
		for (Integer val : set) {
			count += WEIGHTS.get(val);
		}
		
		return count;
	}
	
	public static boolean validSet(Set<Integer> set) {
		
		for (Integer num : set) {
			
			int x = num / 10; 
			int y = num % 10;
			
			if (set.contains(y * 10 + x))
				return false;		
		}
		
		int cal = calculateWeight(set);
		if (weights.contains(cal))
			return false;
		
		weights.add(cal);
	
		return true;
	}
	
	public static String toString(Set<Integer> set) {
		
		StringBuilder builder = new StringBuilder();
		for (Integer val : set) {
			if (builder.length() > 0)
				builder.append(", ");
			
			String tmp = (val < 10) ? "0" + val : String.valueOf(val);
			builder.append(tmp);
		}
		
		return builder.toString();
		
	}

}
