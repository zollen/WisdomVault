import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class HiddenMarkovModelExercise2 {
	
	private static final DecimalFormat ff = new DecimalFormat("0.######");
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/**
		 * states = ('1', '2') observations = ('a', 'b', 'c', 'd')
		 * 
		 * start_probability = {'1': 0.6, '2': 0.4}
		 * 
		 * transition_probability = { 
		 * 				'1' : {'start': 0.6, '1': 0.5, '2': 0.3}, 
		 * 				'2' : {'start': 0.4, '1': 0.5, '2': 0.7}
		 * }
		 * 
		 * emission_probability = { 
		 * 				'1' : {'a': 0.4, 'b': 0.3, 'c': 0.2, 'd': 0.1},
		 * 				'2' : {'a': 0.3, 'b': 0.2, 'c': 0.3, 'd': 0.2}
		 * }
		 */	
		
		Equation eq = new Equation();		 
		eq.process("T = [" +
						/*   S,   b1,   b2,   a1,   a2,   d1,   d2 */
			/* S  */    "  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0;" +
			/* b1 */	"  0.6,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0;" +
			/* b2 */	"  0.4,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0;" +
			/* a1 */	"  0.0, 0.20, 0.12,  0.0,  0.0,  0.0,  0.0;" +
			/* a2 */	"  0.0, 0.15, 0.21,  0.0,  0.0,  0.0,  0.0;" +
			/* d1 */    "  0.0,  0.0,  0.0, 0.05, 0.03,  0.0,  0.0;" +
			/* d2 */    "  0.0,  0.0,  0.0, 0.10, 0.14,  0.0,  0.0 " +
					"]");
		
		Set<Integer> starts = new HashSet<Integer>(); 
		starts.add(0);
		
		Map<Integer, Arc> results = new LinkedHashMap<Integer, Arc>();
		DMatrixRMaj T = eq.lookupDDRM("T");
	
		viterbi(T, starts, results);
		
		results.entrySet().stream().forEach(p -> System.out.println(Arc.STATES[p.getKey()] + " ===> " + p.getValue()));
		
		
		Set<Integer> ends = new HashSet<Integer>(); 
		ends.add(5);
		ends.add(6);
		
		StringBuffer buffer = new StringBuffer();
		traverse(results, ends, buffer);
		
		System.out.println(buffer.toString());
	}
	
	public static void viterbi(DMatrixRMaj graph, Collection<Integer> current, Map<Integer, Arc> results) {
		
		Collection<Integer> nexts = new HashSet<Integer>();
		
		if (current.isEmpty())
			return;
		
		for (int from : current) {
		
			for (int to = 0; to < graph.numRows; to++) {
				
				double cost = 0.0d;
				if (to < graph.numRows && from < graph.numCols)
					cost = (double) graph.get(to, from);
				
				if (cost > 0) {
					
					Arc src = results.get(from);
					Arc dest = results.get(to);
					
					double total = 0d;
					if (src != null)
						total = src.getProb();
					
					if (dest == null || (dest != null && dest.getProb() < total + cost)) {
						results.put(to, new Arc(from, to, total + cost));
						nexts.add(to);
					}
				}
			}
		}
		
		viterbi(graph, nexts, results);
		
	}
	
	public static void traverse(Map<Integer, Arc> results, Collection<Integer> current, StringBuffer output) {
		
		if (current.isEmpty())
			return;
		
		double val = Double.MIN_VALUE;
		int selected = -1;
		
		for (int j : current) {
			
			Arc arc = results.get(j);
			
			if (arc != null && arc.getProb() > val) {
				val = arc.getProb();
				selected = j;
			}
			else 
			if (arc == null) {
				selected = j;
			}
		}
		
		
		Arc arc = results.get(selected);
		if (arc != null) {
			
			Collection<Integer> nexts = new HashSet<Integer>();
			nexts.add(arc.getFrom());
			
			
			traverse(results, nexts, output);
			
			output.append(" ==> " + Arc.STATES[selected]);
		}
		else {
			output.append(Arc.STATES[selected]);
		}
	}
	
	
	public static class Arc {
		
		public static final String [] STATES = { "S", "b1", "b2", "a1", "a2", "d1", "d2" };

		
		private int from = -1;
		private int to = -1;
		private double prob = -1;
		
		public Arc() {}
		
		public Arc(int from, int to, double cost) {
			this.from = from;
			this.to = to;
			this.prob = cost;
		}
		
		public int getFrom() {
			return from;
		}
		public void setFrom(int from) {
			this.from = from;
		}
		public int getTo() {
			return to;
		}
		public void setTo(int to) {
			this.to = to;
		}
		public double getProb() {
			return prob;
		}
		public void setProb(double cost) {
			this.prob = cost;
		}
		@Override
		public String toString() {
			return "Arc [from=" + STATES[from] + ", to=" + STATES[to] + ", prob=" + ff.format(prob) + "]";
		}
	}
}
