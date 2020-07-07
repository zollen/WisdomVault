package genetic;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *  https://github.com/jenetics/jenetics/blob/master/jenetics.prog/README.adoc#examples
 *  
 */
public class JeneticsBasic2 {
	
	// Problem: Replace as many values with zero as neccesary so 
	// all columns and Rows must sum up to 16
	//
	// 2  8  8  6  4
	// 8  2  4  2  12
	// 4  4  2  6  16
	// 6  8  4 12  2
	// 8  6  4  2  4
		
	// There are two starting points
	
	// Scenario #1 - keep the 16: one solution
	// 2  8  8  6  0       0  8  8  0  0
	// 8  2  4  2  0       8  2  4  2  0
	// 0  0  0  0 16   =>  0  0  0  0 16
	// 6  8  4 12  0       0  0  4 12  0
	// 8  6  4  2  0       8  6  0  2  0
	
	// Scenaio #2 - not keep the 16: no solution
	// 2  0  8  6  0
	// 0  2  0  2 12
	// 4  4  2  6  0
	// 6  0  4  0  0
	// 8  6  4  2  4
	
	// Or we can ignore the scenario #1 or #2 just brute force with genertic algorithm.
	
	private static final List<Set<Integer>> entries = new ArrayList<Set<Integer>>();
		
	private static final Random rand = new Random(23);
	
	private static final int [] data = {
			2,  8,  8,  6,  4,
			8,  2,  4,  2, 12,
			4,  4,  2,  6, 16,
			6,  8,  4, 12,  2,
			8,  6,  4,  2,  4
	};


	private static int generation = 1;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		setup();

		// Optimization problem with constraints
		Map<Integer, List<int []>> parents = new LinkedHashMap<Integer, List<int []>>();
		List<int []> first = new ArrayList<int []>();
		first.add(data);
		parents.put(cost(data), first);
			
		int [] target = null;
		
		for (; target == null && generation < 500; generation++) {
			
			Map<Integer, List<int []>> children = new LinkedHashMap<Integer, List<int []>>();
			
			for (int birth = 0; target == null && birth < 1000; birth++) {
				
				int [] child = birth(parents);
				int cost = cost(child);
				
				if (cost == 0) {
					target = child;
					continue;
				}
						
				List<int []> lists = children.get(cost);
				if (lists == null) {
					lists = new ArrayList<int []>();
					children.put(cost, lists);
				}
				lists.add(child);
			}
			
			parents = children;
		}
		
		System.out.println("============  DONE  ============");
		
		if (target != null) {
			System.out.println("Generation: " + generation + ", Jackpot!");
			System.out.println(Arrays.toString(target) + " ==> " + cost(target));
		}
		else {
			System.out.println("Generation: " + generation + ", Population size: " + parents.size());
			Optional<Integer> key = parents.keySet().stream().findFirst();
			
			List<int []> lists = parents.get(key.get());
			
			lists.stream().forEach(p -> System.out.println(Arrays.toString(p) + " ==> " + cost(p)));
		}
	}
	
	public static int [] mate(Map<Integer, List<int []>> parents) {
		
		Optional<Integer> key = parents.keySet().stream().findFirst();
		
		List<int []> lists = parents.get(key.get());
		
		if (lists.size() > 1) {
			
			int [] child = new int[25];
			
			int [] husband = lists.get(0);
			int [] wife = lists.get(rand.nextInt(lists.size() - 1) + 1);
			
			for (int i = 0; i < 25; i++) {
				
				if (husband[i] == wife[i]) {
					child[i] = husband[i];
				}
				else {
					child[i] = rand.nextBoolean() ? husband[i] : wife[i];
				}			
			}
			
			return child;
		}
		
		return Arrays.copyOf(lists.get(0), 25);
	}
	
	public static int [] birth(Map<Integer, List<int []>> parents) {
		
		final int [] child = mate(parents);
		
		
		
		for (int i = 0; i < 25; i++) {
			
			if (generation > 20 && rand.nextDouble() < 0.3)
				continue;
						
			int pos = rand.nextInt(25);
			
			if (child[pos] > 0) {
				
				AtomicInteger num = new AtomicInteger(0);
			
				entries.stream().forEach(p -> {
					
					if (p.contains(pos)) {
						int total = p.stream().mapToInt(g -> child[g]).sum();
						if (total - child[pos] >= 16)
							num.incrementAndGet();
					}
				});
			
				if (num.get() == 2) {
					child[pos] = 0;	
				}
			}
			else {
				child[pos] = data[pos];
			}
		}
		
		return child;
	}

	public static int cost(int [] list) {
		
		int row1 = list[0] + list[1] + list[2] + list[3] + list[4];
		int row2 = list[5] + list[6] + list[7] + list[8] + list[9];
		int row3 = list[10] + list[11] + list[12] + list[13] + list[14];
		int row4 = list[15] + list[16] + list[17] + list[18] + list[19];
		int row5 = list[20] + list[21] + list[22] + list[23] + list[24];
		
		int col1 = list[0] + list[5] + list[10] + list[15] + list[20];
		int col2 = list[1] + list[6] + list[11] + list[16] + list[21];
		int col3 = list[2] + list[7] + list[12] + list[17] + list[22];
		int col4 = list[3] + list[8] + list[13] + list[18] + list[23];
		int col5 = list[4] + list[9] + list[14] + list[19] + list[24];
		
		return Math.abs(16 - row1) + 
				Math.abs(16 - row2) * 50+ 
				Math.abs(16 - row3) * 100 + 
				Math.abs(16 - row4) * 150 + 
				Math.abs(16 - row5) * 200 +
				Math.abs(16 - col1) * 250 + 
				Math.abs(16 - col2) * 300 + 
				Math.abs(16 - col3) * 350 + 
				Math.abs(16 - col4) * 400 + 
				Math.abs(16 - col5) * 450;

	}
	
	private static void setup() {
		
		Set<Integer> row1 = new HashSet<Integer>();
		row1.add(0);
		row1.add(1);
		row1.add(2);
		row1.add(3);
		row1.add(4);
		
		Set<Integer> row2 = new HashSet<Integer>();
		row2.add(5);
		row2.add(6);
		row2.add(7);
		row2.add(8);
		row2.add(9);
		
		Set<Integer> row3 = new HashSet<Integer>();
		row3.add(10);
		row3.add(11);
		row3.add(12);
		row3.add(13);
		row3.add(14);
		
		Set<Integer> row4 = new HashSet<Integer>();
		row4.add(15);
		row4.add(16);
		row4.add(17);
		row4.add(18);
		row4.add(19);
		
		Set<Integer> row5 = new HashSet<Integer>();
		row5.add(20);
		row5.add(21);
		row5.add(22);
		row5.add(23);
		row5.add(24);
		
		
		Set<Integer> col1 = new HashSet<Integer>();
		col1.add(0);
		col1.add(5);
		col1.add(10);
		col1.add(15);
		col1.add(20);
		
		Set<Integer> col2 = new HashSet<Integer>();
		col2.add(1);
		col2.add(6);
		col2.add(11);
		col2.add(16);
		col2.add(21);
		
		Set<Integer> col3 = new HashSet<Integer>();
		col3.add(2);
		col3.add(7);
		col3.add(12);
		col3.add(17);
		col3.add(22);
		
		Set<Integer> col4 = new HashSet<Integer>();
		col4.add(3);
		col4.add(8);
		col4.add(13);
		col4.add(18);
		col4.add(23);
		
		Set<Integer> col5 = new HashSet<Integer>();
		col5.add(4);
		col5.add(9);
		col5.add(14);
		col5.add(19);
		col5.add(24);
		
		entries.add(row1);
		entries.add(row2);
		entries.add(row3);
		entries.add(row4);
		entries.add(row5);
		entries.add(col1);
		entries.add(col2);
		entries.add(col3);
		entries.add(col4);
		entries.add(col5);
	}
	

}