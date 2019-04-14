package optimization.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

/**
 * Kruskal algorithm results in a minimum spanning tree against a
 * connected, uni/by-directional, weighted graph with unknown start and end states.
 * 
 * The algorithm computes the spanning tree of traversing all nodes with the least amount of cost.
 * (without circles)
 * 
 * Difference between uni-directional and bi-directional
 * 	1. register(...)
 * 	2. Edge.equals(...)
 * 
 * https://www.youtube.com/watch?v=71UQH7Pr9kU&t=1s
 * @author zollen
 *
 */
public class KruskalExercise {
	
	private static final String [] LABELS = { "A", "B", "C", "D", "E", "F", "G" };
	
	static {
		BINode.setLABELS(LABELS);
		BIEdge.setLABELS(LABELS);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ " + 
				/*  A,  B,  C,  D,  E,  F,  G */
	/* A */ 	"	0,  2,  3,  3,  0,  0,  0;" +
	/* B */		"   2,  0,  4,  0,  3,  0,  0;" +
	/* C */		"   3,  4,  0,  5,  1,  6,  0;" +
	/* D */		"   3,  0,  5,  0,  0,  7,  0;" +
	/* E */		"   0,  3,  1,  0,  0,  8,  0;" +
	/* F */		"   0,  0,  6,  7,  8,  0,  9;" +
	/* G */		"   0,  0,  0,  0,  0,  9,  0 " +
				"]");
		
		DMatrixRMaj A = eq.lookupDDRM("A");
		
		Set<BIEdge> initial = new HashSet<BIEdge>();
		
		for (int col = 0; col < A.numCols; col++) {
			
			for (int row = 0; row < A.numRows; row++) {
				
				int weight = (int) A.get(row, col);
				
				if (weight > 0) {		
					initial.add(new BIEdge(row, col, weight));
				}
			}
		}
		
		List<BIEdge> list = new ArrayList<BIEdge>(initial);		
		Collections.sort(list, new Comparator<BIEdge>() {

			@Override
			public int compare(BIEdge o1, BIEdge o2) {
				// TODO Auto-generated method stub
				return Integer.valueOf(o1.getWeight()).compareTo(o2.getWeight());
			}			
		});
	
		
		// Kruskal algo begin

		Map<Integer, Set<Integer>> map = new HashMap<Integer, Set<Integer>>();	
		List<BIEdge> pool = new ArrayList<BIEdge>();
		
		while (list.size() > 0) {
			
			BIEdge first = list.stream().findFirst().get();
			list.remove(first);
			
			if (circle(map, first)) {
				continue;
			}
			
			register(map, first);
			
			pool.add(first);
		}
		
		pool.stream().forEach(p -> System.out.println(p));
		System.out.println("Number of edges: " + pool.size());

		List<BINode> results = BINode.construct(pool);
		results.stream().forEach(p -> {
			
			System.out.println("====  Score: [" + p.score() + "]  ===");
			System.out.println(p);
			
		});
	}
	
	public static boolean circle(Map<Integer, Set<Integer>> map, BIEdge target) {
		
		Set<Integer> visited = new HashSet<Integer>();
		visited.add(target.getFrom());
			
		return circle(map, visited, target.getFrom(), target.getTo());
	}
	
	private static boolean circle(Map<Integer, Set<Integer>> map, Set<Integer> visited, 
							int last, int current) {
				
		if (visited.contains(current))
			return true;
		
		visited.add(current);
				
		Set<Integer> set = map.get(current);
		
		if (set != null) {		
			for (Integer s : set) {			
				if (s != last && circle(map, visited, current, s))
					return true;		
			}
		}
		
		return false;
	}
	
	private static void register(Map<Integer, Set<Integer>> map, BIEdge edge) {
			
		register(map, edge.getFrom(), edge.getTo());
		register(map, edge.getTo(), edge.getFrom());
	}
	
	private static void register(Map<Integer, Set<Integer>> map, int from, int to) {
		
		Set<Integer> dests = map.get(from);
		if (dests == null) {
			dests = new HashSet<Integer>();
			map.put(from, dests);
		}
			
		dests.add(to);
	}
}
