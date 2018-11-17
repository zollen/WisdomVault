import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class ViterbiAlgoExercise1 {


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ " +
				 /* A, B, C, D, E, F, G, H, I, J */
		/* A */   " 0, 0, 0, 0, 0, 0, 0, 0, 0, 0;" +
		/* B */   " 0, 0, 0, 0, 0, 0, 0, 0, 0, 0;" +
		/* C */   " 2, 1, 0, 0, 0, 0, 0, 0, 0, 0;" +
		/* D */   " 3, 4, 0, 0, 0, 0, 0, 0, 0, 0;" +
		/* E */   " 0, 2, 0, 0, 0, 0, 0, 0, 0, 0;" +
		/* F */   " 0, 0, 0, 1, 3, 0, 0, 0, 0, 0;" + 
		/* G */   " 0, 0, 1, 1, 0, 0, 0, 0, 0, 0;" + 
		/* H */   " 0, 0, 3, 0, 1, 0, 0, 0, 0, 0;" + 
		/* I */   " 0, 0, 0, 0, 0, 2, 1, 0, 0, 0;" + 
		/* J */   " 0, 0, 0, 0, 0, 3, 0, 2, 0, 0 " + 
				"]");
		
		DMatrixRMaj A = eq.lookupDDRM("A");
		
		Collection<Integer> starts = new HashSet<Integer>();
		starts.add(0);  /* A */
		starts.add(1);  /* B */
	
		Collection<Integer> ends = new HashSet<Integer>();
		ends.add(8);   /* I */
		ends.add(9);   /* J */
		
		Map<Integer, Arc> results = new LinkedHashMap<Integer, Arc>();
		analysis(A, starts, results);
		
		results.entrySet().stream().forEach(p -> System.out.println(Arc.STATES[p.getKey()] + " ===> " + p.getValue()));
		
		StringBuffer buffer = new StringBuffer();
		traverse(results, ends, buffer);
		
		System.out.println(buffer.toString());
	}
	
	public static void traverse(Map<Integer, Arc> results, Collection<Integer> current, StringBuffer output) {
		
		if (current.isEmpty())
			return;
		
		int val = Integer.MAX_VALUE;
		
		for (int j : current) {
			
			if (j < val)
				val = j;
		}
		
		Arc arc = results.get(val);
		if (arc != null) {
			
			Collection<Integer> nexts = new HashSet<Integer>();
			nexts.add(arc.getFrom());
			
			output.append(Arc.STATES[val] + " <== ");
		
			traverse(results, nexts, output);
		}
		else {
			
			output.append(Arc.STATES[val]);
		}
	}
	
	public static void analysis(DMatrixRMaj graph, Collection<Integer> current, Map<Integer, Arc> results) {
		
		Collection<Integer> nexts = new HashSet<Integer>();
		
		if (current.isEmpty())
			return;
		
		for (int j : current) {
		
			for (int i = 0; i < graph.numRows; i++) {
				
				int state = (int) graph.get(i, j);
				
				if (state > 0) {
					
					int jj = min(graph, i);
					if (jj != j)
						continue;
					
					int origcost = (int) graph.get(i, jj);
					
					int cost = 0;
					Arc arc = results.get(jj);
					if (arc != null)
						cost = arc.getCost(); 
					
					results.put(i, new Arc(jj, i, cost + origcost));
					
					nexts.add(i);
				}
			}
		}
		
		analysis(graph, nexts, results);
		
	}
	
	
	
	public static int min(DMatrixRMaj graph, int row) {
		
		int val = Integer.MAX_VALUE;
		int pos = Integer.MIN_VALUE;
		
		for (int i = 0; i < graph.numCols; i++) {
			
			int curr = (int) graph.get(row, i);
			if (curr > 0 && curr < val && i != row) {
				val = curr;
				pos = i;
			}
		}
		
		return pos;
	}
	
	
	public static class Arc {
		
		public static final String [] STATES = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J" };

		
		private int from = -1;
		private int to = -1;
		private int cost = -1;
		
		public Arc() {}
		
		public Arc(int from, int to, int cost) {
			this.from = from;
			this.to = to;
			this.cost = cost;
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
		public int getCost() {
			return cost;
		}
		public void setCost(int cost) {
			this.cost = cost;
		}
		@Override
		public String toString() {
			return "Arc [from=" + STATES[from] + ", to=" + STATES[to] + ", cost=" + cost + "]";
		}
	}
	
}