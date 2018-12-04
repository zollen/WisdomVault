import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class HiddenMarkovModelExercise3 {
	
	private static final DecimalFormat ff = new DecimalFormat("0.######");
	
	private static DMatrixRMaj T = null;
	private static DMatrixRMaj E = null;	
	
	
	private static final String [] STATES = { "A", "B" };
	private static final int STATE_A = 0;
	private static final int STATE_B = 1;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/**
		 * states = ('A', 'B') observations = ('0', '1')
		 * 
		 * start_probability = {
		 * 				'A': 0.99, 
		 * 				'B': 0.01
		 * }
		 * 
		 * transition_probability = { 
		 * 				'A' : { 'A': 0.99, 'B': 0.01 }, 
		 * 				'B' : { 'B': 0.99, 'A': 0.01 }
		 * }
		 * 
		 * emission_probability = { 
		 * 				'A' : { '0': 0.8, '1': 0.2 },
		 * 				'B' : { '0': 0.1, '1': 0.9 }
		 * }
		 */	
		
		Equation eq = new Equation();	
		eq.process("T = [ " +
						/* A,     B */
			/* A */ "   0.99,  0.01;" +
			/* B */ "   0.01,  0.99 " +
						"]");

		eq.process("E = [" +
					/* A,     B */
			/* 0 */ "  0.8,   0.1;" +
			/* 1 */ "  0.2,   0.9 " +
					"]");
		
		
		T = eq.lookupDDRM("T");
		E = eq.lookupDDRM("E");
		
		String [] sequence = { "0", "1", "0" };
		
		{
			// find: P(O1=0, O2=1, O3=0, T1, T2, T3)
			System.out.println("======== [Forward] ==========");
			Map<Integer, Double> starts = new LinkedHashMap<Integer, Double>();
			starts.put(STATE_A, 0.99d);
			starts.put(STATE_B, 0.01d);
		
			forward(starts, sequence);
		}
		
		
		{
			// find: P(O1=0, O2=1, O3=0, T1, T2, T3)
			System.out.println("======== [Backward] ==========");
			Map<Integer, Double> ends = new LinkedHashMap<Integer, Double>();
			ends.put(STATE_A, 1d);
			ends.put(STATE_B, 1d);
			
			backward(ends, sequence);		
		}
		
		System.out.println("============================");
		System.out.println("The final result of both Backend algo and Fordward algo are the same");
		System.out.println();
		
		{
			System.out.println("========== [Forward/Backward] ==========");
			System.out.println("Compute the most likely probabilties of each state");
			System.out.println("Forward1(A) * Backward3(A) = 0.792 * 0.157977 = 0.12511778");
			System.out.println("Forward1(B) * Backward3(B) = 0.001 * 0.096923 = 0.00009692");
			System.out.println("Forward2(A) * Backward2(A) = 0.156818 * 0.793 = 0.12435667");
			System.out.println("Forward2(B) * Backward2(B) = 0.008019 * 0.107 = 0.00085803");
			System.out.println("Forward3(A) * Backward1(A) = 0.124264 * 1 = 0.124264");
			System.out.println("Forward3(B) * Backward1(B) = 0.000951 * 1 = 0.000951");
		}
		
		{
			System.out.println("========== [Viterbi] ==========");
			System.out.println("Compute the most likely sequence of states given the sequence of 010");
			
			eq.process("G = [" +
							/*   S,    0A,     0B,    1A,    1B,    0A,    0B  */
				/* S  */   " 0.000, 0.000,  0.000, 0.000, 0.000, 0.000, 0.000;" +
				/* 0A */   " 0.792, 0.000,  0.000, 0.000, 0.000, 0.000, 0.000;" +
				/* 0B */   " 0.001, 0.000,  0.000, 0.000, 0.000, 0.000, 0.000;" +
				/* 1A */   " 0.000, 0.198,  0.002, 0.000, 0.000, 0.000, 0.000;" +
				/* 1B */   " 0.000, 0.009,  0.891, 0.000, 0.000, 0.000, 0.000;" +
				/* 0A */   " 0.000, 0.000,  0.000, 0.792, 0.008, 0.000, 0.000;" +
				/* 0B */   " 0.000, 0.000,  0.000, 0.001, 0.099, 0.000, 0.000 " +
							"]");
			
			Set<Integer> starts = new HashSet<Integer>(); 
			starts.add(0);
			
			Map<String, Arc> results = new LinkedHashMap<String, Arc>();
			DMatrixRMaj G = eq.lookupDDRM("G");
		
			viterbi(G, starts, 0, results);
			
			results.entrySet().stream().forEach(
					p -> System.out.println(p.getKey() + " ===> " + p.getValue()));	
			
			System.out.println("0A -> 1A -> 0A");
		}
	}
	
	public static void forward(Map<Integer, Double> starts, String [] sequence) {

		Map<String, Double> probs = new LinkedHashMap<String, Double>();
		final Map<String, Double> ss = probs;
		
		starts.entrySet().stream().forEach(p -> { 
				ss.put("0" + String.valueOf(p.getKey()), p.getValue() * E.get(0, p.getKey())); 
		});
		
		Set<Integer> tos = new LinkedHashSet<Integer>(starts.keySet());
		
		for (int observable = 0 + 1; observable < sequence.length; observable++) {
			
			Set<Integer> nexts = new LinkedHashSet<Integer>();

			for (int to : tos) {
				
				{
					double sum = 0d;
					for (int from = 0; from < T.numCols; from++) {
					
						if (T.get(to, from) > 0 && E.get(new Integer(sequence[observable]), to) > 0) {
					
							double last = 0d;
							if (probs.get(String.valueOf(observable - 1) + String.valueOf(from)) != null)
								last = probs.get(String.valueOf(observable - 1) + String.valueOf(from));
					
							sum += (double) last * T.get(to, from) * E.get(new Integer(sequence[observable]), to);
						}
					}
					
					probs.put(String.valueOf(observable) + String.valueOf(to), sum);
				}
				
				
				{
					int from = to;
					for (int _to = 0; _to < T.numRows; _to++) {
						if (T.get(_to, from) > 0)
							nexts.add(_to);
					}
				}
			}
			
			tos = nexts;
		}
		
		probs.entrySet().stream().forEach( p -> System.out.println("OBSERV(" +
				sequence[new Integer(p.getKey().substring(0,  1))] + ") State(" +
				STATES[new Integer(p.getKey().substring(1,  2))] + ") ===> " + 
				ff.format(p.getValue())));
		
		System.out.println("P(O1=0, O2=1, O3=0, T1, T2, T3)");
		System.out.println("Forward3(A) + Forward3(B) = 0.124264 + 0.000951 = 0.125215");
	}
	
	public static void backward(Map<Integer, Double> ends, String [] sequence) {
		
		Map<String, Double> probs = new LinkedHashMap<String, Double>();
		final Map<String, Double> ss = probs;
		
		ends.entrySet().stream().forEach(p -> ss.put(String.valueOf(sequence.length - 1) + String.valueOf(p.getKey()), p.getValue())); 
		
		Set<Integer> froms = new LinkedHashSet<Integer>(ends.keySet());
		
		for (int observable = sequence.length - 2; observable >= 0; observable--) {
			
			Set<Integer> nexts = new LinkedHashSet<Integer>();
			
			for (int from : froms) {
				
				{
					double sum = 0d;				
					for (int to = 0; to < T.numRows; to++) {
				
						if (T.get(to, from) > 0 && E.get(new Integer(sequence[observable]), to) > 0) {
						
							double last = 0d;
							if (probs.get(String.valueOf((observable + 1) + String.valueOf(to))) != null)
								last = probs.get(String.valueOf((observable + 1) + String.valueOf(to)));
						
							sum += (double) last * T.get(to, from) * E.get(new Integer(sequence[observable + 1]), to);
						}
					}

					probs.put(String.valueOf(observable) + String.valueOf(from), sum);
				}
				
				{
					for (int _to = 0; _to < T.numRows; _to++) {
						if (T.get(_to, from) > 0)
							nexts.add(_to);
					}	
				}
			}
				
			froms = nexts;
		}
		
		probs.entrySet().stream().forEach( p -> System.out.println("OBSERV(" +
				sequence[new Integer(p.getKey().substring(0,  1))] + ") State(" +
				STATES[new Integer(p.getKey().substring(1,  2))] + ") ===> " + 
				ff.format(p.getValue())));
		
		System.out.println("P(O1=0, O2=1, O3=0, T1, T2, T3");
		System.out.println("Forward1(A) * Backward3(A) + Forward1(B) * Backward3(B) =  0.792 * 0.157977 + 0.001 * 0.096923 = 0.125215");
	}
	
	public static void viterbi(DMatrixRMaj graph, Collection<Integer> current, int seq, Map<String, Arc> results) {
		
		Collection<Integer> nexts = new LinkedHashSet<Integer>();
		
		if (current.isEmpty())
			return;
		
		for (int from : current) {
		
			for (int to = 0; to < graph.numRows; to++) {
				
				double cost = 0.0d;
				if (to < graph.numRows && from < graph.numCols)
					cost = (double) graph.get(to, from);
				
				if (cost > 0) {
					
					Arc src = results.get(String.valueOf(seq - 1) + "#" + Arc.STATES[from]);
					Arc dest = results.get(String.valueOf(seq) + "#" + Arc.STATES[to]);
					
					double total = 0d;
					if (src != null)
						total = src.getProb();
					
					if (dest == null || (dest != null && dest.getProb() < total + cost)) {
						results.put(String.valueOf(seq) + "#" + Arc.STATES[to], new Arc(from, to, total + cost));
						nexts.add(to);
					}
				}
			}
		}
		
		viterbi(graph, nexts, seq + 1, results);
		
	}
	
	public static class Arc {
		
		public static final String [] STATES = { "S", "0A", "0B", "1A", "1B", "0A", "0B" };

		
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
