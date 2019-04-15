package optimization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;

public class ResourcesAllocationExercise {

	static final String[] STATES = { "Washington", "Oregon", "Nevada", "California", "Montana", "Idaho", "Utah",
			"Arizona", "Wyoming", "Colorado", "New Mexico", "North Dakota", "South Dakota", "Nebraska", "Kansas",
			"Oklahoma", "Texas", "Minnesota", "Iowa", "Missouri", "Arkansas", "Louisiana", "Wisconsin", "Illinois",
			"Michigan", "Indiana", "Ohio", "West Virgina", "Kentuchy", "Tennessee", "Mississippi", "Alabama", "Georgia",
			"Forida", "New York", "Pennsylvania", "Delaware", "Maryland", "Washington DC", "Virgina", "North Carolina",
			"South Carolina", "Vermont", "New Hamphsire", "Massachusetts", "Connecticut", "New Jersey", "Alaska",
			"Hawaii", "Maine", "Rodge Island" };

	// resources, constraint: states that share the border must not share the same
	// color
	static final int RED = 100;
	static final int BLUE = 101;
	static final int GREEN = 102;
	static final int YELLOW = 103;

	static final DMatrixRMaj A = new DMatrixRMaj(STATES.length, STATES.length);

	static final Set<Integer> BAG = new HashSet<Integer>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/**
		 * https://en.wikipedia.org/wiki/List_of_states_and_territories_of_the_United_States#/media/File:Map_of_USA_with_state_names_2.svg
		 */

		Map<String, Integer> states = new HashMap<String, Integer>();
		Map<Integer, Integer> result = new LinkedHashMap<Integer, Integer>();

		for (int i = 0; i < STATES.length; i++) {
			states.put(STATES[i], i);
			result.put(i, 0);
		}

		setup(states);

		BAG.add(RED);
		BAG.add(BLUE);
		BAG.add(GREEN);
		BAG.add(YELLOW);

		result = scan(result);

		result.entrySet().stream().forEach(p -> System.out.println(STATES[p.getKey()] + " --> " + color(p.getValue())));
	}

	public static Map<Integer, Integer> scan(Map<Integer, Integer> result) {

		if (completed(result))
			return result;

		Map<Integer, Integer> _result = new LinkedHashMap<Integer, Integer>(result);

		for (int col = 0; col < A.numCols; col++) {

			if (_result.get(col) > 0)
				continue;

			Set<Integer> colors = check(col, _result);

			for (int row = 0; row < A.numRows; row++) {

				if (colors.size() > 0) {

					for (int color : colors) {

						_result.put(col, color);

						_result = scan(_result);
						if (_result != null)
							break;
					}

					if (_result == null)
						return null; /* invalid branch */
				} else {
					return null; /* invalid branch */
				}
			}
		}

		return _result;
	}

	public static boolean completed(Map<Integer, Integer> result) {

		return result.values().stream().allMatch(p -> p > 0);
	}

	public static Set<Integer> check(int col, Map<Integer, Integer> result) {

		Set<Integer> bag = new HashSet<Integer>(BAG);

		for (int row = 0; row < A.numRows; row++) {

			if (A.get(row, col) > 0) {

				int color = result.get(row);
				if (color > 0) {
					bag.remove(color);
				}
			}
		}

		return bag;
	}

	public static String color(int val) {

		switch (val) {
		case RED:
			return "Red";
		case BLUE:
			return "Blue";
		case GREEN:
			return "Green";
		case YELLOW:
			return "Yellow";
		default:
			return "Not Assigned";
		}
	}

	public static void setup(Map<String, Integer> states) {

		CommonOps_DDRM.fill(A, 0);

		// Washington
		A.set(states.get("Oregon"), states.get("Washington"), 1);
		A.set(states.get("Idaho"), states.get("Washington"), 1);

		// Oregon
		A.set(states.get("Washington"), states.get("Oregon"), 1);
		A.set(states.get("Idaho"), states.get("Oregon"), 1);
		A.set(states.get("California"), states.get("Oregon"), 1);
		A.set(states.get("Nevada"), states.get("Oregon"), 1);

		// California
		A.set(states.get("Oregon"), states.get("California"), 1);
		A.set(states.get("Nevada"), states.get("California"), 1);
		A.set(states.get("Arizona"), states.get("California"), 1);

		// Nevada
		A.set(states.get("California"), states.get("Nevada"), 1);
		A.set(states.get("Oregon"), states.get("Nevada"), 1);
		A.set(states.get("Idaho"), states.get("Nevada"), 1);
		A.set(states.get("Utah"), states.get("Nevada"), 1);
		A.set(states.get("Arizona"), states.get("Nevada"), 1);

		// Idaho
		A.set(states.get("Nevada"), states.get("Idaho"), 1);
		A.set(states.get("Oregon"), states.get("Idaho"), 1);
		A.set(states.get("Utah"), states.get("Idaho"), 1);
		A.set(states.get("Washington"), states.get("Idaho"), 1);
		A.set(states.get("Montana"), states.get("Idaho"), 1);
		A.set(states.get("Wyoming"), states.get("Idaho"), 1);

		// Utah
		A.set(states.get("Idaho"), states.get("Utah"), 1);
		A.set(states.get("Nevada"), states.get("Utah"), 1);
		A.set(states.get("Arizona"), states.get("Utah"), 1);
		A.set(states.get("Wyoming"), states.get("Utah"), 1);
		A.set(states.get("Colorado"), states.get("Utah"), 1);

		// Arizona
		A.set(states.get("Utah"), states.get("Arizona"), 1);
		A.set(states.get("Nevada"), states.get("Arizona"), 1);
		A.set(states.get("California"), states.get("Arizona"), 1);
		A.set(states.get("New Mexico"), states.get("Arizona"), 1);

		// Montana
		A.set(states.get("Idaho"), states.get("Montana"), 1);
		A.set(states.get("Wyoming"), states.get("Montana"), 1);
		A.set(states.get("North Dakota"), states.get("Montana"), 1);
		A.set(states.get("South Dakota"), states.get("Montana"), 1);

		// Wyoming
		A.set(states.get("Montana"), states.get("Wyoming"), 1);
		A.set(states.get("Idaho"), states.get("Wyoming"), 1);
		A.set(states.get("Utah"), states.get("Wyoming"), 1);
		A.set(states.get("Colorado"), states.get("Wyoming"), 1);
		A.set(states.get("South Dakota"), states.get("Wyoming"), 1);
		A.set(states.get("Nebraska"), states.get("Wyoming"), 1);

		// Colorado
		A.set(states.get("Wyoming"), states.get("Colorado"), 1);
		A.set(states.get("Utah"), states.get("Colorado"), 1);
		A.set(states.get("New Mexico"), states.get("Colorado"), 1);
		A.set(states.get("Nebraska"), states.get("Colorado"), 1);
		A.set(states.get("Kansas"), states.get("Colorado"), 1);
		A.set(states.get("Oklahoma"), states.get("Colorado"), 1);

		// New Mexico
		A.set(states.get("Colorado"), states.get("New Mexico"), 1);
		A.set(states.get("Arizona"), states.get("New Mexico"), 1);
		A.set(states.get("Oklahoma"), states.get("New Mexico"), 1);
		A.set(states.get("Texas"), states.get("New Mexico"), 1);

		// North Dakota
		A.set(states.get("Montana"), states.get("North Dakota"), 1);
		A.set(states.get("South Dakota"), states.get("North Dakota"), 1);
		A.set(states.get("Minnesota"), states.get("North Dakota"), 1);

		// South Dakota
		A.set(states.get("North Dakota"), states.get("South Dakota"), 1);
		A.set(states.get("Montana"), states.get("South Dakota"), 1);
		A.set(states.get("Wyoming"), states.get("South Dakota"), 1);
		A.set(states.get("Nebraska"), states.get("South Dakota"), 1);
		A.set(states.get("Minnesota"), states.get("South Dakota"), 1);
		A.set(states.get("Iowa"), states.get("South Dakota"), 1);

		// Nebraska
		A.set(states.get("South Dakota"), states.get("Nebraska"), 1);
		A.set(states.get("Wyoming"), states.get("Nebraska"), 1);
		A.set(states.get("Iowa"), states.get("Nebraska"), 1);
		A.set(states.get("Colorado"), states.get("Nebraska"), 1);
		A.set(states.get("Kansas"), states.get("Nebraska"), 1);
		A.set(states.get("Missouri"), states.get("Nebraska"), 1);

		// Kansas
		A.set(states.get("Nebraska"), states.get("Kansas"), 1);
		A.set(states.get("Colorado"), states.get("Kansas"), 1);
		A.set(states.get("Oklahoma"), states.get("Kansas"), 1);
		A.set(states.get("Missouri"), states.get("Kansas"), 1);

		// Oklahoma
		A.set(states.get("Kansas"), states.get("Oklahoma"), 1);
		A.set(states.get("New Mexico"), states.get("Oklahoma"), 1);
		A.set(states.get("Colorado"), states.get("Oklahoma"), 1);
		A.set(states.get("Missouri"), states.get("Oklahoma"), 1);
		A.set(states.get("Arkansas"), states.get("Oklahoma"), 1);
		A.set(states.get("Texas"), states.get("Oklahoma"), 1);

		// Texas
		A.set(states.get("Oklahoma"), states.get("Texas"), 1);
		A.set(states.get("New Mexico"), states.get("Texas"), 1);
		A.set(states.get("Arkansas"), states.get("Texas"), 1);
		A.set(states.get("Louisiana"), states.get("Texas"), 1);

		// Minnesota
		A.set(states.get("North Dakota"), states.get("Minnesota"), 1);
		A.set(states.get("South Dakota"), states.get("Minnesota"), 1);
		A.set(states.get("Iowa"), states.get("Minnesota"), 1);
		A.set(states.get("Wisconsin"), states.get("Minnesota"), 1);

		// Iowa
		A.set(states.get("Minnesota"), states.get("Iowa"), 1);
		A.set(states.get("South Dakota"), states.get("Iowa"), 1);
		A.set(states.get("Wisconsin"), states.get("Iowa"), 1);
		A.set(states.get("Nebraska"), states.get("Iowa"), 1);
		A.set(states.get("Missouri"), states.get("Iowa"), 1);
		A.set(states.get("Illinois"), states.get("Iowa"), 1);

		// Missouri
		A.set(states.get("Iowa"), states.get("Missouri"), 1);
		A.set(states.get("Nebraska"), states.get("Missouri"), 1);
		A.set(states.get("Illinois"), states.get("Missouri"), 1);
		A.set(states.get("Arkansas"), states.get("Missouri"), 1);
		A.set(states.get("Kansas"), states.get("Missouri"), 1);
		A.set(states.get("Oklahoma"), states.get("Missouri"), 1);
		A.set(states.get("Tennessee"), states.get("Missouri"), 1);
		A.set(states.get("Kentuchy"), states.get("Missouri"), 1);

		// Arkansas
		A.set(states.get("Missouri"), states.get("Arkansas"), 1);
		A.set(states.get("Oklahoma"), states.get("Arkansas"), 1);
		A.set(states.get("Tennessee"), states.get("Arkansas"), 1);
		A.set(states.get("Texas"), states.get("Arkansas"), 1);
		A.set(states.get("Mississippi"), states.get("Arkansas"), 1);
		A.set(states.get("Louisiana"), states.get("Arkansas"), 1);

		// Louisiana
		A.set(states.get("Arkansas"), states.get("Louisiana"), 1);
		A.set(states.get("Texas"), states.get("Louisiana"), 1);
		A.set(states.get("Mississippi"), states.get("Louisiana"), 1);

		// Wisconsin
		A.set(states.get("Minnesota"), states.get("Wisconsin"), 1);
		A.set(states.get("Iowa"), states.get("Wisconsin"), 1);
		A.set(states.get("Illinois"), states.get("Wisconsin"), 1);

		// Illinois
		A.set(states.get("Wisconsin"), states.get("Illinois"), 1);
		A.set(states.get("Iowa"), states.get("Illinois"), 1);
		A.set(states.get("Missouri"), states.get("Illinois"), 1);
		A.set(states.get("Kentuchy"), states.get("Illinois"), 1);
		A.set(states.get("Indiana"), states.get("Illinois"), 1);

		// Michigan
		A.set(states.get("Indiana"), states.get("Michigan"), 1);
		A.set(states.get("Ohio"), states.get("Michigan"), 1);

		// Indiana
		A.set(states.get("Michigan"), states.get("Indiana"), 1);
		A.set(states.get("Illinois"), states.get("Indiana"), 1);
		A.set(states.get("Kentuchy"), states.get("Indiana"), 1);
		A.set(states.get("Ohio"), states.get("Indiana"), 1);

		// Kentucky
		A.set(states.get("Indiana"), states.get("Kentuchy"), 1);
		A.set(states.get("Tennessee"), states.get("Kentuchy"), 1);
		A.set(states.get("Illinois"), states.get("Kentuchy"), 1);
		A.set(states.get("Ohio"), states.get("Kentuchy"), 1);
		A.set(states.get("Missouri"), states.get("Kentuchy"), 1);
		A.set(states.get("West Virgina"), states.get("Kentuchy"), 1);
		A.set(states.get("Virgina"), states.get("Kentuchy"), 1);

		// Ohio
		A.set(states.get("Kentuchy"), states.get("Ohio"), 1);
		A.set(states.get("Michigan"), states.get("Ohio"), 1);
		A.set(states.get("Indiana"), states.get("Ohio"), 1);
		A.set(states.get("West Virgina"), states.get("Ohio"), 1);
		A.set(states.get("Pennsylvania"), states.get("Ohio"), 1);

		// Tennessee
		A.set(states.get("Kentuchy"), states.get("Tennessee"), 1);
		A.set(states.get("Arkansas"), states.get("Tennessee"), 1);
		A.set(states.get("Missouri"), states.get("Tennessee"), 1);
		A.set(states.get("Mississippi"), states.get("Tennessee"), 1);
		A.set(states.get("Alabama"), states.get("Tennessee"), 1);
		A.set(states.get("Georgia"), states.get("Tennessee"), 1);
		A.set(states.get("North Carolina"), states.get("Tennessee"), 1);
		A.set(states.get("Virgina"), states.get("Tennessee"), 1);

		// Mississippi
		A.set(states.get("Tennessee"), states.get("Mississippi"), 1);
		A.set(states.get("Arkansas"), states.get("Mississippi"), 1);
		A.set(states.get("Louisiana"), states.get("Mississippi"), 1);
		A.set(states.get("Alabama"), states.get("Mississippi"), 1);

		// Alabama
		A.set(states.get("Mississippi"), states.get("Alabama"), 1);
		A.set(states.get("Georgia"), states.get("Alabama"), 1);
		A.set(states.get("Tennessee"), states.get("Alabama"), 1);
		A.set(states.get("Forida"), states.get("Alabama"), 1);

		// Georgia
		A.set(states.get("Alabama"), states.get("Georgia"), 1);
		A.set(states.get("Forida"), states.get("Georgia"), 1);
		A.set(states.get("North Carolina"), states.get("Georgia"), 1);
		A.set(states.get("South Carolina"), states.get("Georgia"), 1);
		A.set(states.get("Tennessee"), states.get("Georgia"), 1);

		// Florida
		A.set(states.get("Georgia"), states.get("Forida"), 1);
		A.set(states.get("Alabama"), states.get("Forida"), 1);

		// South Carolina
		A.set(states.get("Georgia"), states.get("South Carolina"), 1);
		A.set(states.get("North Carolina"), states.get("South Carolina"), 1);

		// North Carolina
		A.set(states.get("South Carolina"), states.get("North Carolina"), 1);
		A.set(states.get("Tennessee"), states.get("North Carolina"), 1);
		A.set(states.get("Virgina"), states.get("North Carolina"), 1);
		A.set(states.get("Georgia"), states.get("North Carolina"), 1);

		// Virgina
		A.set(states.get("North Carolina"), states.get("Virgina"), 1);
		A.set(states.get("West Virgina"), states.get("Virgina"), 1);
		A.set(states.get("Maryland"), states.get("Virgina"), 1);
		A.set(states.get("Tennessee"), states.get("Virgina"), 1);
		A.set(states.get("Kentuchy"), states.get("Virgina"), 1);
		A.set(states.get("Washington DC"), states.get("Virgina"), 1);

		// West Virginia
		A.set(states.get("Virgina"), states.get("West Virgina"), 1);
		A.set(states.get("Kentuchy"), states.get("West Virgina"), 1);
		A.set(states.get("Ohio"), states.get("West Virgina"), 1);
		A.set(states.get("Pennsylvania"), states.get("West Virgina"), 1);
		A.set(states.get("Maryland"), states.get("West Virgina"), 1);

		// Maryland
		A.set(states.get("West Virgina"), states.get("Maryland"), 1);
		A.set(states.get("Pennsylvania"), states.get("Maryland"), 1);
		A.set(states.get("Delaware"), states.get("Maryland"), 1);
		A.set(states.get("Virgina"), states.get("Maryland"), 1);
		A.set(states.get("Washington DC"), states.get("Maryland"), 1);

		// Delaware
		A.set(states.get("Maryland"), states.get("Delaware"), 1);
		A.set(states.get("Pennsylvania"), states.get("Delaware"), 1);
		A.set(states.get("New Jersey"), states.get("Delaware"), 1);

		// Washington DC
		A.set(states.get("Virgina"), states.get("Washington DC"), 1);
		A.set(states.get("Maryland"), states.get("Washington DC"), 1);

		// New Jersey
		A.set(states.get("Delaware"), states.get("New Jersey"), 1);
		A.set(states.get("Pennsylvania"), states.get("New Jersey"), 1);
		A.set(states.get("New York"), states.get("New Jersey"), 1);

		// Pennsylvania
		A.set(states.get("New Jersey"), states.get("Pennsylvania"), 1);
		A.set(states.get("New York"), states.get("Pennsylvania"), 1);
		A.set(states.get("Ohio"), states.get("Pennsylvania"), 1);
		A.set(states.get("West Virgina"), states.get("Pennsylvania"), 1);
		A.set(states.get("Maryland"), states.get("Pennsylvania"), 1);
		A.set(states.get("Delaware"), states.get("Pennsylvania"), 1);

		// New York
		A.set(states.get("Pennsylvania"), states.get("New York"), 1);
		A.set(states.get("New Jersey"), states.get("New York"), 1);
		A.set(states.get("Massachusetts"), states.get("New York"), 1);
		A.set(states.get("Connecticut"), states.get("New York"), 1);
		A.set(states.get("Vermont"), states.get("New York"), 1);

		// Massachusetts
		A.set(states.get("New York"), states.get("Massachusetts"), 1);
		A.set(states.get("Connecticut"), states.get("Massachusetts"), 1);
		A.set(states.get("Vermont"), states.get("Massachusetts"), 1);
		A.set(states.get("New Hamphsire"), states.get("Massachusetts"), 1);
		A.set(states.get("Rodge Island"), states.get("Massachusetts"), 1);

		// Vermont
		A.set(states.get("New York"), states.get("Vermont"), 1);
		A.set(states.get("New Hamphsire"), states.get("Vermont"), 1);
		A.set(states.get("Massachusetts"), states.get("Vermont"), 1);

		// New Hamphsire
		A.set(states.get("Vermont"), states.get("New Hamphsire"), 1);
		A.set(states.get("Massachusetts"), states.get("New Hamphsire"), 1);
		A.set(states.get("Maine"), states.get("New Hamphsire"), 1);

		// Connecticut
		A.set(states.get("New York"), states.get("Connecticut"), 1);
		A.set(states.get("Rodge Island"), states.get("Connecticut"), 1);
		A.set(states.get("Massachusetts"), states.get("Connecticut"), 1);

		// Rodge Island
		A.set(states.get("Connecticut"), states.get("Rodge Island"), 1);
		A.set(states.get("Massachusetts"), states.get("Rodge Island"), 1);

		// Maine
		A.set(states.get("New Hamphsire"), states.get("Maine"), 1);
	}

}
