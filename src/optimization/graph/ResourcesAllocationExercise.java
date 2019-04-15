package optimization.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ejml.data.DMatrixRMaj;

/**
 * Prim algorithm results in a minimum spanning tree against a connected,
 * uni/by-directional, weighted graph with unknown start and end states.
 * 
 * The algorithm computes the spanning tree of traversing all nodes with the
 * least amount of cost. (without circles)
 * 
 * Difference between uni-directional and bi-directional 1. register(...) 2.
 * Edge.equals(...)
 * 
 * https://www.youtube.com/watch?v=cplfcGZmX7I
 * 
 * @author zollen
 *
 */
public class ResourcesAllocationExercise {

	static final String[] STATES = { "Washington", "Oregon", "Nevada", "California", "Montana", "Idaho", "Utah",
			"Arizona", "Wyoming", "Colorado", "New Mexico", "North Dakota", "South Dakota", "Nebraska", "Kansas",
			"Oklahoma", "Texas", "Minnesota", "Iowa", "Missouri", "Arkansas", "Louisiana", "Wisconsin", "Illinois",
			"Michigan", "Indiana", "Ohio", "West Virgina", "Kentuchy", "Tennessee", "Mississippi", "Alabama", "Georgia",
			"Forida", "New York", "Pennsylvania", "Delaware", "Maryland", "Washington DC", "Virgina", "North Carolina",
			"South Carolina", "Vermont", "New Hamphsire", "Massachusetts", "Connecticut", "New Jersey", "Alaska",
			"Hawaii", "Maine", "Rodge Island" };

	static {
		BINode.setLABELS(STATES);
		BIEdge.setLABELS(STATES);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/**
		 * https://en.wikipedia.org/wiki/List_of_states_and_territories_of_the_United_States#/media/File:Map_of_USA_with_state_names_2.svg
		 * 
		 * 0. Washington 1. Oregon 2. Nevada 3. California 4. Montana 5. Idaho 6. Utah
		 * 7. Arizona 8. Wyoming 9. Colorado 10. New Mexico 11. North Dakota 12. South
		 * Dakota 13. Nebraska 14. Kansas 15. Oklahoma 16. Texas 17. Minnesota 18. Iowa
		 * 19. Missouri 20. Arkansas 21. Louisiana 22. Wisconsin 23. Illinois 24.
		 * Michigan 25. Indiana 26. Ohio 27. West Virgina 28. Kentuchy 29. Tennessee 30.
		 * Mississippi 31. Alabama 32. Georgia 33. Forida 34. New York 35. Pennsylvania
		 * 36. Delaware 37. Maryland 38. Washington DC 39. Virgina 40. North Carolina
		 * 41. South Carolina 42. Vermont 43. New Hamphsire 44. Massachusetts 45.
		 * Connecticut 46. New Jersey 47. Alaska 48. Hawaii 49. Maine 50 Rodge Island
		 */

		DMatrixRMaj A = new DMatrixRMaj(STATES.length, STATES.length);
		
	

		// Prim Algo begin

		List<BIEdge> pool = new ArrayList<BIEdge>();
		int last = pool.size();

		Set<Integer> visited = new HashSet<Integer>();

		// randomly pick a start node
		visited.add(0); /* let's pick A */

		while (pool.size() < STATES.length - 1) {

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
