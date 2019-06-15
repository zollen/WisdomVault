package genetic;

import java.util.List;

public interface Individual {
	
	public Double score(Individual ideal);
	
	public Individual mate(List<Individual> individuals);
	
	public Individual variation(double param);
	
	public Individual mutation(double param);
	
	public String toString();
}
