package optimization;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class DijkstraExercise {
	
	private static DMatrixRMaj A = null;
	

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
		vertices.put(0, 0);
		for (int i = 1; i <= 6; i++)
			vertices.put(i, Integer.MAX_VALUE);
			
		
		A = eq.lookupDDRM("A");
		
		dijkstra(0, vertices, new LinkedHashMap<Integer, Integer>(), new HashSet<Integer>());
		
	}
	
	public static void dijkstra(int state, Map<Integer, Integer> vertices, 
			Map<Integer, Integer> paths, Set<Integer> states) {
		
		Set<Integer> _states = new HashSet<Integer>(states);
		_states.add(state);
		Map<Integer, Integer> _vertices = new LinkedHashMap<Integer, Integer>();
		_vertices.putAll(vertices);
		Map<Integer, Integer> _paths = new LinkedHashMap<Integer, Integer>();
		_paths.putAll(paths);
				
		for (int row = 0; row < A.numRows; row++) {
			
			if (A.get(row, state) > 0) {
				
				int dist = _vertices.get(state) + (int) A.get(row, state);
				
				if (!_states.contains(row) && dist < _vertices.get(row)) {
					_vertices.put(row, dist);
					_paths.put(state, row);
				}
				
				if (!_states.contains(row) && row != 5) {
					dijkstra(row, _vertices, _paths, _states);
				}
				else {
					if (row == 5) {
						print(_vertices, _paths);	
					}
				}
			}	
		}
	}
	
	public static void print(Map<Integer, Integer> vertices, Map<Integer, Integer> paths) {
		
		final String [] labels = { "A", "B", "C", "D", "E", "F", "G" };
		
		StringBuilder builder = new StringBuilder();
		paths.entrySet().stream().forEach(p -> builder.append(labels[p.getKey()] + " ===> " + 
										labels[p.getValue()] + ", "));
		
		System.out.println(builder.toString() + " : " + vertices.get(5));
	}

}
