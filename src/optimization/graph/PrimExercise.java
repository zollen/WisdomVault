package optimization.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

/**
 * Prim algorithm results in a minimum spanning tree against a
 * connected, uni/by-directional, weighted graph with unknown start and end states.
 * 
 * The algorithm computes the spanning tree of traversing all nodes with the least amount of cost.
 * (without circles)
 * 
 * Difference between uni-directional and bi-directional
 * 	1. register(...)
 * 	2. Edge.equals(...)
 * 
 * https://www.youtube.com/watch?v=cplfcGZmX7I
 * @author zollen
 *
 */
public class PrimExercise {
	
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
		
		// Prim Algo begin
		
		List<BIEdge> pool = new ArrayList<BIEdge>();
		int last = pool.size();
		
		Set<Integer> visited = new HashSet<Integer>();
		
		// randomly pick a start node
		visited.add(0);   /* let's pick A */
			
		while (pool.size() < LABELS.length - 1) {
			
			last = pool.size();
			
			int min = Integer.MAX_VALUE;
			BIEdge edge = null;
			for (Integer from : visited) {
			
				for (int to = 0; to < A.numRows; to++) {
					int dist = (int) A.get(to, from);
					if (dist > 0 && min > dist && !visited.contains(to)) {
						min = dist;
						edge = new BIEdge(to, from, dist);
					}
				}
			}
		
			if (edge != null) {
				visited.add(edge.getTo());
				pool.add(edge);
			}
			
			if (last == pool.size())
				break;
		}
		
		pool.stream().forEach(p -> System.out.println(p));
		System.out.println("Number of edges: " + pool.size());

		List<BINode> results = BINode.construct(pool);
		results.stream().forEach(p -> {
			
			System.out.println("====  Score: [" + p.score() + "]  ===");
			System.out.println(p);
			
		});

	}
}
