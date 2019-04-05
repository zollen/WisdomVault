package optimization;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class DijkstraExercise {
	
	private static final int START_STATE_A = 0;
	private static final int END_STATE_F = 5;
	
	private static DMatrixRMaj A = null;

	private static Map<Integer, List<Map<Integer, Integer>>> results = new TreeMap<Integer, List<Map<Integer, Integer>>>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Alternative algorithm of Viterbi for finding the shortest path
		
		// A - 4 - B
		// A - 3 - C
		// A - 7 - E
		// B - 6 - C
		// B - 5 - D
		// C - 11 - D
		// D - 2 - E
		// D - 2 - F
		// D - 10 - G
		// D - 2 - F
		// G - 3 - F
		Equation eq = new Equation();
		eq.process("A = [ " + 
				 //    A,  B,  C,  D,  E,  F,  G
			/* A */ "  0,  4,  3,  0,  7,  0,  0;" +
			/* B */	"  4,  0,  6,  5,  0,  0,  0;" +
			/* C */ "  3,  6,  0, 11,  8,  0,  0;" +
			/* D */ "  0,  5, 11,  0,  2,  2,  0;" +
			/* E */ "  7,  0,  8,  2,  0,  0,  5;" +
			/* F */ "  0,  0,  0,  2,  0,  0,  3;" +
			/* G */ "  0,  0,  0, 10,  5,  3,  0 " +
		"]"); 
		
		// Answers: A -> B -> D -> F
		

		Map<Integer, Integer> vertices = new LinkedHashMap<Integer, Integer>();
		vertices.put(START_STATE_A, 0);
		for (int i = 1; i <= 6; i++)
			vertices.put(i, Integer.MAX_VALUE);
			
		
		A = eq.lookupDDRM("A");
		
		dijkstra(START_STATE_A, vertices, new LinkedHashMap<Integer, Integer>(), new HashSet<Integer>());
		
		Map.Entry<Integer, List<Map<Integer, Integer>>> first = results.entrySet().stream().findFirst().get();
		
		first.getValue().stream().forEach(p -> print(first.getKey(), p));

	}
	
	public static void dijkstra(int state, Map<Integer, Integer> vertices, 
			Map<Integer, Integer> paths, Set<Integer> states) {
		
		Set<Integer> _states = new HashSet<Integer>(states);
		_states.add(state);
		
		Map<Integer, Integer> _vertices = new LinkedHashMap<Integer, Integer>(vertices);
		Map<Integer, Integer> _paths = new LinkedHashMap<Integer, Integer>(paths);
		
				
		for (int row = START_STATE_A; row < A.numRows; row++) {
			
			if (A.get(row, state) > 0) {
				
				int dist = _vertices.get(state) + (int) A.get(row, state);
				
				if (!_states.contains(row) && dist < _vertices.get(row)) {
					_vertices.put(row, dist);
					_paths.put(state, row);
				}
				
				if (!_states.contains(row) && row != END_STATE_F) {
					dijkstra(row, _vertices, _paths, _states);
				}
				else {
					if (_paths.values().stream().reduce(
							(first, second) -> second).orElse(null) == END_STATE_F) {
						
						int score = _vertices.get(END_STATE_F);
		
						List<Map<Integer, Integer>> list = results.get(score);
						
						if (list == null) {
							list = new ArrayList<Map<Integer, Integer>>();							
							results.put(score, list);
						}
						
						list.add(new LinkedHashMap<Integer, Integer>(_paths));
					}
				}
			}	
		}
	}
	
	public static void print(int scores, Map<Integer, Integer> paths) {
		
		final String [] labels = { "A", "B", "C", "D", "E", "F", "G" };
		
	
		String tmp = paths.entrySet().stream().map(p -> labels[p.getKey()] + " ==> " + 
										labels[p.getValue()]).collect(Collectors.joining(", "));
		
		System.out.println(tmp + " : " + scores);
	}

}
