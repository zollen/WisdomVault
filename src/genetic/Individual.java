package genetic;

import java.util.List;

public interface Individual {
	
	public Double score(Individual ideal);
	
	public Individual mate(double crossOverRate, double mutationRate, 
						List<Individual> individuals);
	
	public Individual variation(double param);
	
	public Individual mutation(double param);
	
	public String toString();
}
