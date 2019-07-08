package machinelearning.classifier;

public class NavieBayesClassifier2 {
	
	/**
	 * Fever: F
	 * Headache: HA
	 * Chest Pain: CP
	 * Nausea: N
	 * Vomit: V
	 * 
	 * Pregnancy: P
	 * Heart Disease: HD
	 * Common Cold: CC
	 * 
	 * Size of the completed T/F table: 2^7 = 128 entries.
	 * 
	 * Independence Map (Assuming F, N, V, HA, CP are all independent from each other)
	 * ----------------
	 * F --+--- CC
	 *     |
	 * N --+--- V --- P
	 *  
	 * HA----+
	 *       |
	 * CP----+--- HD
	 * 
	 * It is sometimes impossible to come up a complete T/F table if you have large amount of variables
	 * (BeliefNet: NaveBayesClassifier.java)
	 * It is better to use probabilities inferences (NaveBayes) to figure out what we need.
	 * 
	 * It is often easier to find the probabilities of the symptoms from a confirmed diseases 
	 * patients, then find out the probabilities of the diseases with nothing but symptoms.
	 * P(F,N|CC) = 0.89
	 * P(F|CC) = 0.72
	 * P(N|CC) = 0.21 
	 * P(N|V) = 0.92
	 * P(V|P) = 0.5
	 * P(HA,CP|HD) = 0.81
	 * P(HA|HD) = 0.10
	 * P(CP|HD) = 0.67 
	 * 
	 * General Form
	 * ------------
	 * P(x1,x2,x3...xn) = P(xn|xn-1,xn-2,...x1)P(xn-1|xn-2,xn-3...x1)P(xn-2|xn-3,xn-4...x1)...P(x1)
	 * 
	 * No loops in the Independence table.
	 * 
	 * Removing each node that has no children, and place the node into the following linear queue.
	 * 
	 * Ordering
	 * --------
	 * HD, CP, HA, P, V, CC, N, F  (non of each descendants appear to each left)
	 * 
	 * P(HD,CP,HA,P,V,CC,N,F) = P(HD|CP,HA,P,V,CC,N,F)P(CP|HA,P,V,CC,N,F)P(HA|P,V,CC,N,F)
	 * 							P(P|V,CC,N,F)P(V|CC,N,F)P(CC|N,F)P(N|F)P(F)
	 * 
	 * 
	 * Any variable is only dependent on the variable directly above but nothing else. 
	 * Therefore we can reduce the above question as follow:
	 * 
	 * P(HD,CP,HA,P,V,CC,N,F) = P(HD|CP,HA)P(CP)P(HA)P(P|V)P(CC|N,F)P(N)P(F)
	 * 						  = P(HD|CP)P(HD|HA)P(CP)P(HA)P(P|V)P(CC|N)P(CC|F)P(N)P(F)
	 * With this equation, I can easily extrapolate the probability of any combination of
	 * inputs of P(HD,CP,HA,P,V,CC,N,F) without the needs of a complete T/F table.
	 * 
	 * Now we construct a small T/F table for each 
	 *   P(HD|CP): 4 entries
	 *   P(HD|HA): 4 entries, 
	 *   P(CP): one value, 
	 *   P(HA): one value, 
	 *   P(P|V): 4 entries, 
	 *   P(CC|N) : 4 entries, 
	 *   P(N): one value, 
	 *   P(F): one value
	 * by using our sample data to start tracking & counting each occurrences.
	 * 							
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}
	
	

}
